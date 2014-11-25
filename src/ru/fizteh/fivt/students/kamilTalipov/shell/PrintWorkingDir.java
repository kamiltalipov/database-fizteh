package ru.fizteh.fivt.students.kamilTalipov.shell;

public class PrintWorkingDir extends SimpleCommand {
    public PrintWorkingDir() {
        super("pwd", 0);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }
        System.out.println(shell.getCurrentPath());
    }
}
