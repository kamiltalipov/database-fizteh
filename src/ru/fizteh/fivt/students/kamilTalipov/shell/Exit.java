package ru.fizteh.fivt.students.kamilTalipov.shell;

public class Exit extends SimpleCommand {
    public Exit() {
        super("exit", 0);
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }
        shell.exit();
    }
}
