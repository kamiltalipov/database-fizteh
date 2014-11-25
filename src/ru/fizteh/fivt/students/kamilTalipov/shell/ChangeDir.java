package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.File;

public class ChangeDir extends SimpleCommand {
    public ChangeDir() {
        super("cd", 1);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }

        File newPath = new File(PathUtils.getPath(args[0], shell.getCurrentPath()));
        if (!newPath.exists()) {
            throw new IllegalArgumentException("cd: '" + args[0] + "': No such file or directory");
        } else if (!newPath.isDirectory()) {
            throw new IllegalArgumentException("cd: '" + args[0] + "': Not a directory");
        }
        shell.setCurrentPath(PathUtils.normalizePath(newPath.getAbsolutePath()));
    }
}
