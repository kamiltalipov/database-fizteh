package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.File;

public class MakeDir extends SimpleCommand {
    public MakeDir() {
        super("mkdir", 1);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }

        String dirName = args[0];
        File file = new File(PathUtils.getPath(dirName, shell.getCurrentPath()));
        if (file.exists()) {
            throw new IllegalArgumentException("mkdir: cannot create directory '" + dirName + "': File exist");
        }
        if (!file.mkdir()) {
            throw new IllegalArgumentException("mkdir: failed to create directory '" + dirName + "'");
        }
    }
}
