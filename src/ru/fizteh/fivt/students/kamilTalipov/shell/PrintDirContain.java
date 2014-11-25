package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.File;
import java.util.Arrays;

public class PrintDirContain extends SimpleCommand {
    PrintDirContain() {
        super("dir", 0);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }

        File currentDir = new File(shell.getCurrentPath());
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            throw new IllegalArgumentException("dir: '" + shell.getCurrentPath() + "' cannot be shows");
        }

        try {
            File[] files = currentDir.listFiles();
            Arrays.sort(files);
            for (File file : files) {
                System.out.println(file.getName());
            }
        } catch (NullPointerException e) {
           //Empty folder
        }
    }
}
