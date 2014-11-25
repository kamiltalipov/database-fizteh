package ru.fizteh.fivt.students.kamilTalipov.database.servlet;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;

import java.util.HashMap;

public class TransactionHandler {
    private final TransactionManager transactionManager;

    private final MultiFileHashTable table;
    private TransactionData transactionData;
    private String id;

    public TransactionHandler(MultiFileHashTable table, TransactionManager transactionManager) {
        this.table = table;
        this.transactionManager = transactionManager;
        id = null;
    }

    public HashMap<String, Storeable> getDiff() {
        if (!isStarted()) {
            id = transactionManager.startTransaction(table);
            transactionData = transactionManager.getTransaction(id);
        }
        return transactionData.getDiff();
    }

    public boolean isStarted() {
        return id != null;
    }

    public void stop() {
        if (id != null) {
            transactionManager.stopTransaction(id);
            id = null;
        }
    }
}
