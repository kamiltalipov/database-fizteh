package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.students.kamilTalipov.database.servlet.server.ServletServer;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class ServerStartCommand extends SimpleCommand {
    private final ServletServer server;

    public ServerStartCommand(ServletServer server) {
            super("starthttp", 1);
        this.server = server;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments < args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        int port = 8080;
        if (args.length == 1) {
            try {
                port = Integer.valueOf(args[0]);
            }  catch (NumberFormatException e) {
                System.out.println("not started: Can not parse port number");
                return;
            }
        }

        try {
            server.start(port);
        }  catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("started at " + port);
    }
}
