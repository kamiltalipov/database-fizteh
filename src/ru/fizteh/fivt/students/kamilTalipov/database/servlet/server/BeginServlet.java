package ru.fizteh.fivt.students.kamilTalipov.database.servlet.server;

import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BeginServlet extends HttpServlet {
    private final MultiFileHashTableProvider provider;

    public BeginServlet(MultiFileHashTableProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("table");
        if (name == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Table name expected");
            return;
        }
        MultiFileHashTable table = provider.getTable(name);
        if (table == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Table does not exist");
        }

        String id = provider.getTransactionManager().startTransaction(table);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");
        resp.getWriter().println(String.format("tid=%s", id));
    }
}
