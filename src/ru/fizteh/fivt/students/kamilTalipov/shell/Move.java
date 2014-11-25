package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.File;

public class Move extends SimpleCommand {
    public Move() {
        super("mv", 2);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }

        String sourceFileName = args[0];
        String destinationFileName = args[1];
        File sourceFile = new File(PathUtils.getPath(sourceFileName, shell.getCurrentPath()));
        File destinationFile = new File(PathUtils.getPath(destinationFileName, shell.getCurrentPath()));
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("mv: Cannot stat '" + args[0] + "': No such file or directory");
        }
        if (destinationFile.exists() && destinationFile.isDirectory()) {
            destinationFile = new File(PathUtils.getPath(destinationFileName + File.separator
                                        + sourceFile.getName(), shell.getCurrentPath()));
        }

        boolean success = sourceFile.renameTo(destinationFile);
        if (!success) {
            throw new IllegalArgumentException("mv: cannot move '" + sourceFileName + "' to '"
                                                + destinationFileName + "'");
        }
    }
}
