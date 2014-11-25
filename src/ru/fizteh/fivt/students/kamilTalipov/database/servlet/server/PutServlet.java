package ru.fizteh.fivt.students.kamilTalipov.database.servlet.server;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.servlet.TransactionData;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public class PutServlet extends HttpServlet {
    private final MultiFileHashTableProvider provider;

    public PutServlet(MultiFileHashTableProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
        String valueString = req.getParameter("value");
        if (valueString == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "value expected");
            return;
        }

        TransactionData transaction;
        try {
            transaction = provider.getTransactionManager().getTransaction(id);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        Storeable value;
        try {
            value = provider.deserialize(transaction.getTable(), valueString);
        } catch (ParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        Storeable oldValue = transaction.getTable().put(key, value, transaction.getDiff());
        String answer;
        try {
            if (oldValue == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "new");
                return;
            } else {
                answer = provider.serialize(transaction.getTable(), oldValue);
            }
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
