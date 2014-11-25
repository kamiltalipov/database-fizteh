package ru.fizteh.fivt.students.kamilTalipov.database.utils;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class StoreableUtils {
    public static boolean isCorrectStoreable(Storeable value, Table table) {
        if (value == null) {
            throw new IllegalArgumentException("Storeable must be not null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        if (!isCorrectSize(value, table)) {
            return false;
        }

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null
                    && value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCorrectSize(Storeable value, Table table) {
        try {
            value.getColumnAt(table.getColumnsCount() - 1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        try {
            value.getColumnAt(table.getColumnsCount());
        }  catch (IndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }

    public static boolean isEqualStoreable(Storeable storeable1, Storeable storeable2) {
        if (storeable1 == null) {
            if (storeable2 == null) {
                return true;
            }
            return false;
        }
        if (storeable2 == null) {
            return false;
        }

        int size = 0;
        while (true) {
            boolean wasException1 = false;
            try {
                storeable1.getColumnAt(size);
            }  catch (IndexOutOfBoundsException e) {
                wasException1 = true;
            }

            boolean wasException2 = false;
            try {
                storeable2.getColumnAt(size);
            }  catch (IndexOutOfBoundsException e) {
                wasException2 = true;
            }

            if (wasException1 != wasException2) {
                return false;
            } else if (wasException1) {
                break;
            }

            ++size;
        }

        for (int i = 0; i < size; ++i) {
            Object object1 = storeable1.getColumnAt(i);
            Object object2 = storeable2.getColumnAt(i);
            if (object1 == null) {
                if (object2 != null) {
                    return false;
                }
                continue;
            }
            if (object2 == null) {
                return false;
            }

            if (object1.getClass() != object2.getClass()) {
                return false;
            }
            if (!object1.toString().equals(object2.toString())) {
                return false;
            }
        }

        return true;
    }
}
