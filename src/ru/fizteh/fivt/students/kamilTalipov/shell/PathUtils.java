package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.File;
import java.io.IOException;

public class PathUtils {
    public static String normalizePath(String path) {
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            return new File(path).getAbsolutePath();
        }
    }

    public static String getPath(String fileName, String currentPath) {
        File file = new File(fileName);
        if (file.isAbsolute()) {
            return normalizePath(file.getAbsolutePath());
        } else {
            return normalizePath(new File(currentPath + File.separator + fileName).getAbsolutePath());
        }
    }
}
