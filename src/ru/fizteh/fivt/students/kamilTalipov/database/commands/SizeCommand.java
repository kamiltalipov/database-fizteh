package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.students.kamilTalipov.database.core.Database;
import ru.fizteh.fivt.students.kamilTalipov.database.core.NoTableSelectedException;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class SizeCommand extends SimpleCommand {
    private final Database database;

    public SizeCommand(Database database) {
        super("size", 0);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        try {
            System.out.println(database.size());
        } catch (NoTableSelectedException e) {
            System.err.println("no table");
        }
    }
}
