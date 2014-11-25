package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.File;

public class Remove extends SimpleCommand {
    public Remove() {
        super("rm", 1);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        File file = new File(PathUtils.getPath(args[0], shell.getCurrentPath()));
        if (!file.exists()) {
            throw new IllegalArgumentException("rm: cannot delete '" + args[0] + "': No such file or directory");
        }
        if (file.isDirectory()) {
            try {
                File[] innerFiles = file.listFiles();
                for (File innerFile : innerFiles) {
                    run(shell, new String[] {args[0] + File.separator + innerFile.getName()});
                }
            }  catch (NullPointerException e) {
                //Empty folder
            }
        }

        boolean success = file.delete();
        if (!success) {
            throw new IllegalArgumentException("rm: cannot delete '" + args[0] + "'");
        }
    }
}
