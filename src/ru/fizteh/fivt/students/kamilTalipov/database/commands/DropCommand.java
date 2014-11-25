package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiTableDatabase;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class DropCommand extends SimpleCommand {
    private final MultiTableDatabase database;

    public DropCommand(MultiTableDatabase database) {
        super("drop", 1);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        if (database.dropTable(args[0])) {
            System.out.println("dropped");
        }  else {
            System.out.println(args[0] + " not exists");
        }
    }
}
