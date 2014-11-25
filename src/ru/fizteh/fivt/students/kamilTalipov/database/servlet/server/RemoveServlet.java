package ru.fizteh.fivt.students.kamilTalipov.database.servlet.server;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.servlet.TransactionData;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RemoveServlet extends HttpServlet {
    private final MultiFileHashTableProvider provider;

    public RemoveServlet(MultiFileHashTableProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String id = req.getParameter("tid");
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transaction id expected");
            return;
        }
        String key = req.getParameter("key");
        if (key == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "key expected");
            return;
        }
        TransactionData transaction;
        try {
            transaction = provider.getTransactionManager().getTransaction(id);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        Storeable value = transaction.getTable().remove(key, transaction.getDiff());
        if (value == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not found");
            return;
        }
        String answer;
        try {
            answer = provider.serialize(transaction.getTable(), value);
        } catch (ColumnFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");
        resp.getWriter().println(answer);
    }
}
