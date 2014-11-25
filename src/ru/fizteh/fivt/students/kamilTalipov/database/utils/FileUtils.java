package ru.fizteh.fivt.students.kamilTalipov.database.utils;

import ru.fizteh.fivt.students.kamilTalipov.shell.PathUtils;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static File makeDir(String absolutePath) throws IllegalArgumentException, IOException {
        File file = new File(absolutePath);
        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("Path '" + absolutePath + "' not absolute");
        }
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("File '" + absolutePath + "' exist, but it's not directory");
            }
            return file;
        }
        if (!file.mkdir()) {
            throw new IOException("failed to create directory '" + absolutePath + "'");
        }

        return file;
    }

    public static File makeDirs(String absolutePath) throws IllegalArgumentException, IOException {
        File file = new File(absolutePath);
        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("Path '" + absolutePath + "' not absolute");
        }
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("File '" + absolutePath + "' exist, but it's not directory");
            }
            return file;
        }
        if (!file.mkdirs()) {
            throw new IOException("failed to create directory's '" + absolutePath + "'");
        }

        return file;
    }

    public static File makeDir(String currentPath, String dirName) throws IllegalArgumentException {
        File file = new File(PathUtils.getPath(dirName, currentPath));
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("file '" + dirName + "' exist, but it's not a directory");
            }
            return file;
        }
        if (!file.mkdir()) {
            throw new IllegalArgumentException("failed to create directory '" + dirName + "'");
        }

        return file;
    }

    public static File makeFile(String currentPath, String fileName) throws IllegalArgumentException {
        File file = new File(PathUtils.getPath(fileName, currentPath));
        if (file.exists()) {
            if (!file.isFile()) {
                throw new IllegalArgumentException("file '" + fileName + "' exist, but it's not a file");
            }
            return file;
        }
        try {
            if (!file.createNewFile()) {
                throw new IllegalArgumentException("failed to create file '" + fileName + "'");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to create file '" + fileName + "'");
        }

        return file;
    }

    public static void remove(String currentPath, String name) throws IllegalArgumentException {
        File file = new File(PathUtils.getPath(name, currentPath));
        remove(file);
    }

    public static void remove(File file) throws IllegalArgumentException {
        if (!file.exists()) {
            throw new IllegalArgumentException("cannot delete '" + file.getName()
                                                + "': No such file or directory");
        }
        if (file.isDirectory()) {
            File[] innerFiles = file.listFiles();
            for (File innerFile : innerFiles) {
                remove(file.getAbsolutePath(), innerFile.getName());
            }
        }

        boolean success = file.delete();
        if (!success) {
            throw new IllegalArgumentException("cannot delete '" + file.getName() + "'");
        }
    }
}
