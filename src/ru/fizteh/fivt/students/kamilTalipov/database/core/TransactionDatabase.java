package ru.fizteh.fivt.students.kamilTalipov.database.core;

import java.io.IOException;

public interface TransactionDatabase extends Database {
    int commit() throws IOException;

    int rollback();
}
