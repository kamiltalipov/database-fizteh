package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiTableDatabase;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

import java.util.ArrayList;

public class CreateCommand extends SimpleCommand {
    private final MultiTableDatabase database;

    public CreateCommand(MultiTableDatabase database) {
        super("create", -2);
        this.database = database;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException(name + ": expected at least 2 arguments "
                                                + args.length + " got");
        }

        ArrayList<Class<?>> types = new ArrayList<>();
        clearBrackets(args);
        for (int i = 1; i < args.length; ++i) {
            switch (args[i]) {
                case "int":
                    types.add(Integer.class);
                    break;

                case "long":
                    types.add(Long.class);
                    break;

                case "byte":
                    types.add(Byte.class);
                    break;

                case "float":
                    types.add(Float.class);
                    break;

                case "double":
                    types.add(Double.class);
                    break;

                case "boolean":
                    types.add(Boolean.class);
                    break;

                case "String":
                    types.add(String.class);
                    break;

                case "":
                    break;

                default:
                    System.err.println("wrong type (unsupported type " + args[i] + ")");
                    return;
            }
        }

        try {
            if (database.createTable(args[0], types)) {
                System.out.println("created");
            }  else {
                System.out.println(args[0] + " exists");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    private void clearBrackets(String[] args) {
        args[1] = args[1].replaceFirst("\\(", "");
        args[args.length - 1] = args[args.length - 1].replaceFirst("\\)", "");
    }
}
