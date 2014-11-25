package ru.fizteh.fivt.students.kamilTalipov.database.core;

import java.util.List;

public interface MultiTableDatabase extends Database {
    boolean createTable(String tableName, List<Class<?>> values);

    boolean dropTable(String tableName);

    int setActiveTable(String tableName);
}
