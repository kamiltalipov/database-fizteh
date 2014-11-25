package ru.fizteh.fivt.students.kamilTalipov.database.test;


import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.DatabaseException;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.core.TableRow;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static ru.fizteh.fivt.students.kamilTalipov.database.utils.StoreableUtils.isEqualStoreable;

public class TableTester {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public MultiFileHashTableProvider provider;
    public MultiFileHashTable table;

    @Before
    public void tableInit() throws IOException, DatabaseException {
        provider = new MultiFileHashTableProvider(folder.getRoot().getAbsolutePath());

        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        table = provider.createTable("Test", types);
    }

    @Test
    public void getNameTest() {
        Assert.assertEquals(table.getName(), "Test");
    }

    @Test
    public void putGetCommitTest() throws IOException {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        table.put("123", storeable);
        Assert.assertEquals(table.get("123").toString(), storeable.toString());
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.size(), 1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesPutTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(1, 2));
        table.put("123", storeable);
    }

    @Test
    public void removeTest() {
        Assert.assertEquals(table.remove("fff"), null);

        Storeable storeable = new TableRow(table, Arrays.asList(42, "don't panic"));
        table.put("answer", storeable);
        Assert.assertEquals(table.remove("answer").toString(), storeable.toString());
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void rollbackTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        Assert.assertEquals(table.put("fits", storeable), null);
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertEquals(table.get("fits"), null);
    }

    @Test
    public void overwriteTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        table.put("123", storeable);
        Assert.assertEquals(table.put("123", storeable).toString(), storeable.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullTest() {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        table.remove(null);
    }

    @Test
    public void commitRollbackTest() throws IOException {
        Assert.assertEquals(table.commit(), 0);
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        Assert.assertEquals(table.put("fits", storeable), null);
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("fits", storeable).toString(), storeable.toString());
        Assert.assertEquals(table.commit(), 0);
        table.put("fits", new TableRow(table, Arrays.asList(2, "hfello")));
        table.remove("fits");
        Assert.assertEquals(table.rollback(), 1);
    }

    @Test
    public void commitRollbackTest2() throws IOException {
        Assert.assertEquals(table.commit(), 0);
        Assert.assertEquals(table.size(), 0);
        Storeable storeable = new TableRow(table, Arrays.asList(222, "gell"));
        Storeable storeable2 = new TableRow(table, Arrays.asList(202, "ffll"));
        Assert.assertEquals(table.put("fsss", storeable), null);
        Assert.assertEquals(table.put("fsss", storeable2), storeable);
        Assert.assertEquals(table.remove("fsss"), storeable2);
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void commitRollbackTest3() {
        Assert.assertEquals(table.size(), 0);
        table.remove("test");
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void removePutTest() throws IOException {
        Assert.assertEquals(table.size(), 0);
        Storeable storeable = new TableRow(table, Arrays.asList(222, "gell"));
        table.put("test", storeable);
        table.commit();
        table.remove("test");
        table.put("test", storeable);
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectStoreableShouldFailedTest() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Double.class);
        MultiFileHashTable table2 = provider.createTable("qqqq", types);
        Storeable storeable = provider.createFor(table2, Arrays.asList(2.3));
        table.put("gg", storeable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putUnsupportedTypeShouldFailedTest() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(BigInteger.class);
        MultiFileHashTable table2 = provider.createTable("TypesTest", types);
    }


    @Test
    public void parallelGetTest() throws IOException, ExecutionException, InterruptedException {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        table.put("123", storeable);
        table.commit();

        Callable<Storeable> task = new Callable<Storeable>() {
            @Override
            public Storeable call() throws Exception {
                return table.get("123");
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Future<Storeable> result1 = executor.submit(task);
            Future<Storeable> result2 = executor.submit(task);
            Assert.assertEquals(isEqualStoreable(result1.get(), storeable), true);
            Assert.assertEquals(isEqualStoreable(result2.get(), storeable), true);
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Timeout");
            }
        }
    }

    @Test
    public void parallelGetPutTest() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
                    table.put("555", storeable);
                    try {
                        table.commit();
                    } catch (IOException e) {
                        throw new IllegalStateException("Bad test", e);
                    }
                }
            });
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Storeable result = table.get("555");
                    while (result == null) {
                        result = table.get("555");
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            });
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Timeout");
            }
        }
    }

    @Test
    public void parallelRemoveTest() throws ExecutionException, InterruptedException, IOException {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        table.put("123", storeable);
        table.commit();

        final ReentrantLock lock = new ReentrantLock();

        Callable<Storeable> task = new Callable<Storeable>() {
            @Override
            public Storeable call() throws Exception {
                lock.lock();
                Storeable oldValue;
                try {
                    oldValue = table.remove("123");
                    table.commit();
                } finally {
                    lock.unlock();
                }
                return oldValue;
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Future<Storeable> result1 = executor.submit(task);
            Future<Storeable> result2 = executor.submit(task);
            if (isEqualStoreable(result1.get(), storeable)) {
                if (!isEqualStoreable(result2.get(), null)) {
                    throw new IllegalStateException("Bad test");
                }
            } else if (!isEqualStoreable(result2.get(), storeable)) {
                throw new IllegalStateException("Bad test");
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Timeout");
            }
        }
    }
}
