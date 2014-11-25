package ru.fizteh.fivt.students.kamilTalipov.database.commands;

import ru.fizteh.fivt.students.kamilTalipov.database.servlet.server.ServletServer;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.SimpleCommand;

public class ServerStopCommand extends SimpleCommand {
    private final ServletServer server;

    public ServerStopCommand(ServletServer server) {
        super("stophttp", 1);
        this.server = server;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {
        if (numberOfArguments < args.length) {
            throw new IllegalArgumentException(name + ": expected " + numberOfArguments
                    + " but " + args.length + " got");
        }

        if (!server.isStarted()) {
            System.out.println("not started");
            return;
        }

        int port;
        try {
            port = server.stop();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("stopped at " + port);
    }
}
