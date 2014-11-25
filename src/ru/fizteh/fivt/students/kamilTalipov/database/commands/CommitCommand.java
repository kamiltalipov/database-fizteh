package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.students.kamilTalipov.database.core.NoTableSelectedException;
import ru.fizteh.fivt.students.kamilTalipov.database.core.TransactionDatabase;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

import java.io.IOException;

public class CommitCommand extends SimpleCommand {
    private final TransactionDatabase database;

    public CommitCommand(TransactionDatabase database) {
        super("commit", 0);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments != args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        try {
            System.out.println(database.commit());
        } catch (NoTableSelectedException e) {
            System.err.println("no table");
        } catch (IOException e) {
            System.err.println("io error");
        }
    }
}
