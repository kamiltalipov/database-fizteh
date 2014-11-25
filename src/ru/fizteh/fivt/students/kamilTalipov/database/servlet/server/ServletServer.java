package ru.fizteh.fivt.students.kamilTalipov.database.servlet.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;

public class ServletServer {
    private final MultiFileHashTableProvider provider;
    private Server server;
    private int port;

    public ServletServer(MultiFileHashTableProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider should be not null");
        }

        this.provider = provider;
        server = null;
    }

    public void start(int port) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new BeginServlet(provider)), "/begin");
        context.addServlet(new ServletHolder(new GetServlet(provider)), "/get");
        context.addServlet(new ServletHolder(new PutServlet(provider)), "/put");
        context.addServlet(new ServletHolder(new RemoveServlet(provider)), "/remove");
        context.addServlet(new ServletHolder(new CommitServlet(provider)), "/commit");
        context.addServlet(new ServletHolder(new RollbackServlet(provider)), "/rollback");
        context.addServlet(new ServletHolder(new SizeServlet(provider)), "/size");

        server = new Server(port);
        server.setHandler(context);
        server.start();
        this.port = port;
    }

    public int stop() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
        return port;
    }

    public boolean isStarted() {
        return server != null;
    }

}
