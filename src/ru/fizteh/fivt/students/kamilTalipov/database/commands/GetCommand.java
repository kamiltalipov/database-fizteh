package ru.fizteh.fivt.students.kamilTalipov.database.commands;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.Database;
import ru.fizteh.fivt.students.kamilTalipov.database.core.NoTableSelectedException;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class GetCommand extends SimpleCommand {
    private final Database database;

    public GetCommand(Database database) {
        super("get", 1);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                                                + " but " + args.length + " got");
        }

        try {
            Storeable value = database.get(args[0]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found\n" + database.serialize(value));
            }
        } catch (NoTableSelectedException e) {
            System.err.println("no table");
        }
    }
}
