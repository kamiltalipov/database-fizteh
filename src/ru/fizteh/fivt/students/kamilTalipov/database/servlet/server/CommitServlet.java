package ru.fizteh.fivt.students.kamilTalipov.database.servlet.server;

import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.servlet.TransactionData;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommitServlet extends HttpServlet {
    private final MultiFileHashTableProvider provider;

    public CommitServlet(MultiFileHashTableProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("tid");
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transaction id expected");
            return;
        }
        TransactionData transaction;
        try {
            transaction = provider.getTransactionManager().getTransaction(id);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        int changes = transaction.getTable().commit(transaction.getDiff());
        provider.getTransactionManager().stopTransaction(id);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");
        resp.getWriter().println(String.format("diff=%d", changes));
    }
}
