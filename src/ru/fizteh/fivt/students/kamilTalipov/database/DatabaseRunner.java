package ru.fizteh.fivt.students.kamilTalipov.database;

import ru.fizteh.fivt.students.kamilTalipov.database.commands.*;
import ru.fizteh.fivt.students.kamilTalipov.database.core.HashDatabase;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableFactory;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.servlet.server.ServletServer;
import ru.fizteh.fivt.students.kamilTalipov.shell.Command;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.Exit;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DatabaseRunner {
    public static void main(String[] args) {
        HashDatabase database = null;
        ServletServer server = null;
        try {
            MultiFileHashTableProvider provider = new MultiFileHashTableFactory().create(
                                                                System.getProperty("fizteh.db.dir"));
            database = new HashDatabase(provider);
            server = new ServletServer(provider);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Command[] commands = new Command[] {new PutCommand(database),
                new GetCommand(database),
                new RemoveCommand(database),
                new CreateCommand(database),
                new DropCommand(database),
                new UseCommand(database),
                new SizeCommand(database),
                new CommitCommand(database),
                new RollbackCommand(database),
                new ServerStartCommand(server),
                new ServerStopCommand(server),
                new Exit()};
        try {
            Shell.run(commands, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
