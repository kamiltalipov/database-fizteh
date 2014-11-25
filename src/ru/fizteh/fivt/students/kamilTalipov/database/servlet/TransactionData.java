package ru.fizteh.fivt.students.kamilTalipov.database.servlet;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;

import java.util.HashMap;

public class TransactionData {
    private final MultiFileHashTable table;
    private final HashMap<String, Storeable> diff;

    public TransactionData(MultiFileHashTable table) {
        this.table = table;
        diff = new HashMap<>();
    }

    public MultiFileHashTable getTable() {
        return table;
    }

    public HashMap<String, Storeable> getDiff() {
        return diff;
    }
}
