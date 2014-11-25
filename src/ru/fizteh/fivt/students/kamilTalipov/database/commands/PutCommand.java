package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.Database;
import ru.fizteh.fivt.students.kamilTalipov.database.core.NoTableSelectedException;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

import java.text.ParseException;

public class PutCommand extends SimpleCommand {
    private final Database database;

    public PutCommand(Database database) {
        super("put", -2);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException(name + ": need at least two arguments");
        }
        StringBuilder value = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            value.append(args[i]);
            if (i != args.length - 1) {
                value.append(" ");
            }
        }

        try {
            Storeable oldValue = database.put(args[0], value.toString());
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(database.serialize(oldValue));
            }
        } catch (NoTableSelectedException e) {
            System.err.println("no table");
        }  catch (ColumnFormatException e) {
            System.err.println("Incorrect column type");
        } catch (ParseException e) {
            System.err.println("JSON: incorrect format");
        }
    }
}
