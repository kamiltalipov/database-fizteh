package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class Copy extends SimpleCommand {
    public Copy() {
        super("cp", 2);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }

        String sourceFileName = PathUtils.getPath(args[0], shell.getCurrentPath());
        String destinationFileName = PathUtils.getPath(args[1], shell.getCurrentPath());
        File sourceFile = new File(sourceFileName);
        File destinationFile = new File(destinationFileName);
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("cp: cannot stat '" + args[0] + "': No such file or directory");
        }

        if (destinationFile.exists() && destinationFile.isDirectory()) {
            if (destinationFile.equals(sourceFile)
                    || destinationFile.getAbsolutePath().contains(sourceFile.getAbsolutePath())) {
                throw new IllegalArgumentException("cp: cannot copy same directory to directory");
            }
            destinationFile = new File(destinationFileName + File.separator + sourceFile.getName());
        }
        if (destinationFile.equals(sourceFile)) {
            throw new IllegalArgumentException("cp: cannot copy same file to file");
        }

        if (sourceFile.isDirectory()) {
            copyDir(shell, sourceFile, destinationFile);
        } else if (sourceFile.isFile()) {
            copyFile(sourceFile, destinationFile);
        } else {
            throw new IllegalArgumentException("cp: '" + args[0] + "' No such file or directory");
        }
    }

    private void copyDir(Shell shell, File sourceFile,
                            File destinationFile) throws IllegalArgumentException {
        if (!destinationFile.mkdir()) {
            throw new IllegalArgumentException("cp: cannot create directory '"
                                                + destinationFile.getName() + "'");
        }

        String[] innerFilesNames = sourceFile.list();
        for (String fileName : innerFilesNames) {
            run(shell, new String[] {sourceFile.getAbsolutePath() + File.separator
                                        + fileName, destinationFile.getAbsolutePath()});
        }
    }

    private static void copyFile(File sourceFile, File destinationFile) throws IllegalArgumentException {
        FileInputStream fileIn;
        try {
            fileIn = new FileInputStream(sourceFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("cp: cannot stat '" + sourceFile.getName()
                                                + "': No such file or directory");
        }
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(destinationFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("cp: cannot access to '" + destinationFile.getName() + "'");
        }

        byte[] buff = new byte[BUFF_SIZE];
        int lengthRead = -1;
        try {
            while ((lengthRead = fileIn.read(buff)) > 0) {
                fileOut.write(buff, 0, lengthRead);
            }
        } catch (IOException e) {
            destinationFile.delete();
            throw new IllegalArgumentException("cp: cannot copy '" + sourceFile.getName() + "' to '"
                                                + destinationFile.getName() + "'");
        }

        try {
            fileIn.close();
            fileOut.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't close file");
        }
    }

    private static final int BUFF_SIZE = 1024;
}
