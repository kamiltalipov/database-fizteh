package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kamilTalipov.database.servlet.TransactionHandler;
import ru.fizteh.fivt.students.kamilTalipov.database.servlet.TransactionManager;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.FileUtils;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.JsonUtils;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.StoreableUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static ru.fizteh.fivt.students.kamilTalipov.database.utils.InputStreamUtils.readInt;
import static ru.fizteh.fivt.students.kamilTalipov.database.utils.InputStreamUtils.readString;
import static ru.fizteh.fivt.students.kamilTalipov.database.utils.StoreableUtils.isEqualStoreable;

public class MultiFileHashTable implements Table, AutoCloseable {
    private final HashMap<String, Storeable>[][] table;
    private final ThreadLocal<TransactionHandler> transaction;
    private final TransactionManager manager;

    private final ArrayList<Class<?>> types;

    private final String tableName;
    private final File tableDirectory;

    private final MultiFileHashTableProvider myTableProvider;

    private volatile boolean isRemoved = false;
    private volatile boolean isClosed = false;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private static final int ALL_DIRECTORIES = 16;
    private static final int FILES_IN_DIRECTORY = 16;

    private static final int MAX_KEY_LEN = 1 << 24;
    private static final int MAX_VALUE_LEN = 1 << 24;

    private static final String SIGNATURE_FILE_NAME = "signature.tsv";

    public MultiFileHashTable(String workingDirectory, String tableName,
                               MultiFileHashTableProvider myTableProvider,
                               List<Class<?>> types) throws DatabaseException, IOException {
        if (workingDirectory == null) {
            throw new IllegalArgumentException("Working directory path must be not null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("Table name must be not null");
        }
        if (myTableProvider == null) {
            throw new IllegalArgumentException("Table provider must be not null");
        }
        if (types == null) {
            throw new IllegalArgumentException("wrong type (types must be not null)");
        }
        if (types.isEmpty()) {
            throw new IllegalArgumentException("wrong type (types must be not empty)");
        }

        this.tableName = tableName;

        this.myTableProvider = myTableProvider;
        this.manager = myTableProvider.getTransactionManager();

        this.types = new ArrayList<>();
        for (Class<?> type : types) {
            if (type == null) {
                throw new IllegalArgumentException("wrong type (type must be not null)");
            }
            if (!isSupportedType(type)) {
                throw new IllegalArgumentException("wrong type (unsupported table type "
                        + type.getCanonicalName() + ")");
            }
            this.types.add(type);
        }

        try {
            tableDirectory = FileUtils.makeDir(workingDirectory + File.separator + tableName);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Couldn't open table '" + tableName + "'");
        }

        writeSignatureFile();

        table = new HashMap[ALL_DIRECTORIES][FILES_IN_DIRECTORY];
        transaction = new ThreadLocal<TransactionHandler>() {
            @Override
            protected TransactionHandler initialValue() {
                return manager.createHandlerFor(MultiFileHashTable.this);
            }
        };
        readTable();
    }

    public MultiFileHashTable(String workingDirectory, String tableName,
                              MultiFileHashTableProvider myTableProvider) throws DatabaseException,
            IOException {
        this(workingDirectory, tableName, myTableProvider, getTypes(workingDirectory, tableName));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + tableDirectory.getAbsolutePath() + "]";
    }

    @Override
    public String getName() {
        checkState();
        return tableName;
    }

    public Storeable get(String key, HashMap<String, Storeable> diff) {
        checkState();
        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must be not empty");
        }

        if (diff.containsKey(key)) {
            return diff.get(key);
        }

        readLock.lock();
        try {
            return getFromTable(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        return get(key, transaction.get().getDiff());
    }

    public Storeable put(String key, Storeable value,
                         HashMap<String, Storeable> diff) throws IllegalArgumentException {
        checkState();

        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must be not empty");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("Key must not contain whitespace");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value must be not null");
        }
        if (!StoreableUtils.isCorrectStoreable(value, this)) {
            throw new ColumnFormatException("Storeable incorrect value");
        }

        Storeable oldValue = get(key, diff);
        diff.put(key, value);

        return oldValue;
    }

    @Override
    public Storeable put(String key, Storeable value) {
        return put(key, value, transaction.get().getDiff());
    }

    public Storeable remove(String key, HashMap<String, Storeable> diff) throws IllegalArgumentException {
        checkState();

        if (key == null) {
            throw new IllegalArgumentException("Key must be not null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must be not empty");
        }

        Storeable oldValue;
        readLock.lock();
        try {
            oldValue = get(key, diff);
        } finally {
            readLock.unlock();
        }
        diff.put(key, null);

        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        return remove(key, transaction.get().getDiff());
    }

    public void removeTable() throws DatabaseException {
        checkState();

        writeLock.lock();
        try {
            isRemoved = true;
            removeDataFiles();
            FileUtils.remove(tableDirectory);
        } finally {
            writeLock.unlock();
        }
    }

    public int size(HashMap<String, Storeable> diff) {
        checkState();
        readLock.lock();
        try {
            int tableSize = getTableSize();
            for (Map.Entry<String, Storeable> entry : diff.entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                Storeable savedValue = getFromTable(key);
                if (savedValue == null) {
                    if (value != null) {
                        ++tableSize;
                    }
                } else {
                    if (value == null) {
                        --tableSize;
                    }
                }
            }

            return tableSize;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        return size(transaction.get().getDiff());
    }

    public int commit(HashMap<String, Storeable> diff) throws IOException {
        checkState();

        writeLock.lock();
        try {
            int changes = 0;
            int tableSize = getTableSize();
            HashSet<ChangedFile> changesFile = new HashSet<>();

            for (Map.Entry<String, Storeable> entry : diff.entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                if (value == null) {
                    if (removeFromTable(key) != null) {
                        ++changes;
                        --tableSize;
                        changesFile.add(new ChangedFile(key));
                    }
                } else {
                    Storeable oldValue = putToTable(key, value);
                    if (oldValue == null) {
                        ++tableSize;
                    }
                    if (!isEqualStoreable(value, oldValue)) {
                        ++changes;
                        changesFile.add(new ChangedFile(key));
                    }
                }
            }

            try {
                writeChanges(changesFile);
            } catch (DatabaseException e) {
                throw new IOException("Database io error", e);
            }

            transaction.get().getDiff().clear();

            return changes;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        return commit(transaction.get().getDiff());
    }

    public int rollback(HashMap<String, Storeable> diff) {
        checkState();

        int changes = uncommittedChanges(diff);
        diff.clear();
        return changes;
    }

    @Override
    public int rollback() {
        return rollback(transaction.get().getDiff());
    }

    @Override
    public int getColumnsCount() {
        checkState();
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkState();

        return types.get(columnIndex);
    }

    @Override
    public void close() {
        close(true);
    }

    public int uncommittedChanges() {
        return uncommittedChanges(transaction.get().getDiff());
    }

    public int uncommittedChanges(HashMap<String, Storeable> diff) {
        checkState();
        readLock.lock();
        try {
            int changes = 0;
            for (Map.Entry<String, Storeable> entry : diff.entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                if (!isEqualStoreable(value, getFromTable(key))) {
                    ++changes;
                }
            }

            return changes;
        } finally {
            readLock.unlock();
        }
    }

    void close(boolean needInformProvider) {
        if (isClosed) {
            return;
        }

        if (needInformProvider) {
            myTableProvider.closedTable(this);
        }

        rollback();
        isClosed = true;
    }

    private void checkState() {
        if (isRemoved) {
            throw new IllegalStateException("Table '" + tableName + "' is removed");
        }
        if (isClosed) {
            throw new IllegalStateException("Table '" + tableName + "' is closed");
        }
    }

    private void readTable() throws DatabaseException, FileNotFoundException {
        File[] innerFiles = tableDirectory.listFiles();
        for (File file : innerFiles) {
            if (!file.isDirectory()) {
                if (file.getName().equals(SIGNATURE_FILE_NAME)) {
                    continue;
                }
            }
            if (!file.isDirectory()
                    || (file.isDirectory() && !isCorrectDirectoryName(file.getName()))) {
                throw new DatabaseException("At table '" + tableName
                        + "': directory contain redundant files ");
            }

            readData(file);
        }
    }

    private void writeChanges(HashSet<ChangedFile> changes) throws DatabaseException, IOException {
        if (changes.isEmpty()) {
            return;
        }

        removeChangesFile(changes);

        for (ChangedFile file : changes) {
            if (table[file.directoryId][file.fileId] == null) {
                throw new DatabaseException("Table [" + file.directoryId + "][" + file.fileId + "] "
                        + "expected not null");
            }
            if (table[file.directoryId][file.fileId].isEmpty()) {
                continue;
            }

            File directory = FileUtils.makeDir(tableDirectory.getAbsolutePath()
                    + File.separator + file.directoryId + ".dir");
            File dbFile = FileUtils.makeFile(directory.getAbsolutePath(), file.fileId + ".dat");

            try (FileOutputStream output = new FileOutputStream(dbFile, true)) {
                for (Map.Entry<String, Storeable> entry : table[file.directoryId][file.fileId].entrySet()) {
                    byte[] key = entry.getKey().getBytes(StandardCharsets.UTF_8);
                    byte[] value = serialize(entry.getValue()).getBytes(StandardCharsets.UTF_8);

                    output.write(ByteBuffer.allocate(4).putInt(key.length).array());
                    output.write(ByteBuffer.allocate(4).putInt(value.length).array());
                    output.write(key);
                    output.write(value);
                }
            }
        }
    }

    private static List<Class<?>> getTypes(String workingDirectory,
                                           String tableName) throws IOException {
        File signatureFile = new File(workingDirectory + File.separator + tableName
                + File.separator + SIGNATURE_FILE_NAME);
        if (!signatureFile.exists()) {
            throw new IOException("Signature file is not exist (table '" + tableName + "')");
        }
        ArrayList<Class<?>> types = new ArrayList<>();
        try (FileInputStream signatureStream = new FileInputStream(signatureFile);
             Scanner signatureScanner = new Scanner(signatureStream)) {
            if (!signatureScanner.hasNextLine()) {
                throw new IOException("Signature file is empty (table '" + tableName + "')");
            }

            while (signatureScanner.hasNextLine()) {
                String[] inputTypes = signatureScanner.nextLine().trim().split("\\s+");
                if (inputTypes.length == 0) {
                    throw new IOException("Signature file is empty (table '" + tableName + "')");
                }
                for (String type : inputTypes) {
                    switch (type) {
                        case "int":
                            types.add(Integer.class);
                            break;

                        case "long":
                            types.add(Long.class);
                            break;

                        case "byte":
                            types.add(Byte.class);
                            break;

                        case "float":
                            types.add(Float.class);
                            break;

                        case "double":
                            types.add(Double.class);
                            break;

                        case "boolean":
                            types.add(Boolean.class);
                            break;

                        case "String":
                            types.add(String.class);
                            break;

                        default:
                            throw new IOException("Signature file contain unsupported type '"
                                    + type + "' (table '" + tableName + "')");
                    }
                }
            }
        }

        return types;
    }

    private void writeSignatureFile() throws IOException {
        File signatureFile = FileUtils.makeFile(tableDirectory.getAbsolutePath(), SIGNATURE_FILE_NAME);
        try (FileWriter signatureFileWriter = new FileWriter(signatureFile);
             BufferedWriter signatureWriter = new BufferedWriter(signatureFileWriter)) {
            for (int i = 0; i < getColumnsCount(); ++i) {
                if (getColumnType(i).equals(Integer.class)) {
                    signatureWriter.write("int");
                } else if (getColumnType(i).equals(Long.class)) {
                    signatureWriter.write("long");
                } else if (getColumnType(i).equals(Byte.class)) {
                    signatureWriter.write("byte");
                } else if (getColumnType(i).equals(Float.class)) {
                    signatureWriter.write("float");
                } else if (getColumnType(i).equals(Double.class)) {
                    signatureWriter.write("double");
                } else if (getColumnType(i).equals(Boolean.class)) {
                    signatureWriter.write("boolean");
                } else if (getColumnType(i).equals(String.class)) {
                    signatureWriter.write("String");
                } else {
                    throw new IllegalArgumentException("wrong type (unsupported type "
                            + getColumnType(i).getCanonicalName() + ")");
                }

                if (i != getColumnsCount() - 1) {
                    signatureWriter.write(" ");
                }
            }
        }
    }

    private static int getDirectoryId(byte keyByte) {
        if (keyByte < 0) {
            keyByte *= -1;
        }

        return (keyByte % ALL_DIRECTORIES + ALL_DIRECTORIES) % ALL_DIRECTORIES;
    }

    private static int getFileId(byte keyByte) {
        if (keyByte < 0) {
            keyByte *= -1;
        }

        return ((keyByte / ALL_DIRECTORIES)
                + FILES_IN_DIRECTORY) % FILES_IN_DIRECTORY;
    }

    private HashMap<String, Storeable> getKeyTable(String key, boolean needCreate) {
        byte keyByte = key.getBytes(StandardCharsets.UTF_8)[0];
        int directoryId = getDirectoryId(keyByte);
        int fileId = getFileId(keyByte);
        if (table[directoryId][fileId] == null && needCreate) {
            table[directoryId][fileId] = new HashMap<>();
        }
        return table[directoryId][fileId];
    }

    private int getTableSize() {
        int size = 0;
        for (int i = 0; i < ALL_DIRECTORIES; ++i) {
            for (int j = 0; j < FILES_IN_DIRECTORY; ++j) {
                if (table[i][j] != null) {
                    size += table[i][j].size();
                }
            }
        }
        return size;
    }

    private Storeable getFromTable(String key) {
        HashMap<String, Storeable> table = getKeyTable(key, false);
        if (table == null) {
            return null;
        }
        return table.get(key);
    }

    private Storeable putToTable(String key, Storeable value) {
        return getKeyTable(key, true).put(key, value);
    }

    private Storeable removeFromTable(String key) {
        HashMap<String, Storeable> table = getKeyTable(key, false);
        if (table == null) {
            return null;
        }
        return table.remove(key);
    }

    private static String getDirectoryName(byte keyByte) {
        return Integer.toString(getDirectoryId(keyByte)) + ".dir";
    }

    private static String getFileName(byte keyByte) {
        return Integer.toString(getFileId(keyByte)) + ".dat";
    }

    private static boolean isCorrectDirectoryName(String name) {
        for (int i = 0; i < ALL_DIRECTORIES; ++i) {
            if (name.equals(Integer.toString(i) + ".dir")) {
                return true;
            }
        }

        return false;
    }

    private void readData(File dbDir) throws DatabaseException, FileNotFoundException {
        File[] innerFiles = dbDir.listFiles();
        if (innerFiles.length == 0) {
            throw new DatabaseException("Empty database dir '" + dbDir.getAbsolutePath() + "'");
        }

        for (File dbFile : innerFiles) {
            boolean wasRead = false;
            try (FileInputStream input = new FileInputStream(dbFile)) {
                while (input.available() > 0) {
                    int keyLen = readInt(input);
                    int valueLen = readInt(input);
                    if (keyLen > MAX_KEY_LEN || valueLen > MAX_VALUE_LEN) {
                        throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                                + "' have incorrect format");
                    }
                    String key = readString(input, keyLen);
                    if (!getDirectoryName(key.getBytes(StandardCharsets.UTF_8)[0]).equals(dbDir.getName())
                            || !getFileName(key.getBytes(StandardCharsets.UTF_8)[0]).equals(dbFile.getName())) {
                        throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                                + "' have incorrect format");
                    }
                    String value = readString(input, valueLen);
                    try {
                        putToTable(key, deserialize(value));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Database file '" + dbFile.getAbsolutePath()
                                + "' have incorrect format");
                    }
                    wasRead = true;
                }
            } catch (IOException e) {
                throw new DatabaseException("Database file '" + dbFile.getAbsolutePath()
                        + "' have incorrect format");
            }
            if (!wasRead) {
                throw new DatabaseException("Empty database file '" + dbFile.getAbsolutePath() + "'");
            }
        }
    }

    private void removeChangesFile(HashSet<ChangedFile> changes) throws DatabaseException {
        for (ChangedFile file : changes) {
            File fileToDelete = new File(tableDirectory.getAbsoluteFile() + File.separator
                    + file.getPath());
            if (fileToDelete.exists()) {
                FileUtils.remove(fileToDelete);
            }
        }
    }

    private void removeDataFiles() throws DatabaseException {
        File[] innerFiles = tableDirectory.listFiles();
        for (File file : innerFiles) {
            if (file.isDirectory() && !isCorrectDirectoryName(file.getName())) {
                throw new DatabaseException("At table '" + tableName
                        + "': directory contain redundant files");
            }
            if (!file.isDirectory() && !file.getName().equals(SIGNATURE_FILE_NAME)) {
                throw new DatabaseException("At table '" + tableName
                        + "': directory contain redundant files");
            }
            FileUtils.remove(file);
        }
    }

    private String serialize(Storeable value) {
        return JsonUtils.serialize(value, this);
    }

    private Storeable deserialize(String value) {
        Storeable result;
        try {
            result = JsonUtils.deserialize(value, myTableProvider, this);
        }  catch (ParseException e) {
            throw new IllegalArgumentException("Can't get value '" + value + "'", e);
        }
        return result;
    }

    private static boolean isSupportedType(Class<?> type) {
        return type == Integer.class
                || type == Long.class
                || type == Byte.class
                || type == Float.class
                || type == Double.class
                || type == Boolean.class
                || type == String.class;
    }

    private class ChangedFile {
        public final int directoryId;
        public final int fileId;

        ChangedFile(String key) {
            byte keyByte = key.getBytes(StandardCharsets.UTF_8)[0];
            directoryId = getDirectoryId(keyByte);
            fileId = getFileId(keyByte);
        }

        public String getPath() {
            return directoryId + ".dir" + File.separator + fileId + ".dat";
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object == this) {
                return true;
            }

            if (!(object instanceof ChangedFile)) {
                return false;
            }
            ChangedFile file = (ChangedFile) object;
            return this.fileId == file.fileId && this.directoryId == file.directoryId;
        }

        @Override
        public int hashCode() {
            return directoryId * FILES_IN_DIRECTORY + fileId;
        }
    }
}
