package ru.fizteh.fivt.students.kamilTalipov.database.core;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class TableRow implements Storeable {
    private final List<Class<?>> types;
    private final List<Object> values;

    public TableRow(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        types = new ArrayList<>();
        values = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
            values.add(null);
        }
    }

    public TableRow(Table table, List<?> values) {
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Values must be not null");
        }
        if (table.getColumnsCount() != values.size()) {
            throw new IllegalArgumentException("Values more then columns");
        }

        types = new ArrayList<>();
        this.values = new ArrayList<>();
        for (int i = 0; i < values.size(); ++i) {
            if (values.get(i) != null && table.getColumnType(i) != values.get(i).getClass()) {
                throw new ColumnFormatException("Type at " + i + " mismatch");
            }

            types.add(table.getColumnType(i));
            this.values.add(values.get(i));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        for (int i = 0; i < values.size(); ++i) {
            if (values.get(i) != null) {
                builder.append(values.get(i).toString());
            }
            if (i != values.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null && types.get(columnIndex) != value.getClass()) {
            throw new ColumnFormatException("Type at " + columnIndex + " mismatch");
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Integer) getValueAt(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Long) getValueAt(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Byte) getValueAt(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
       return (Float) getValueAt(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Double) getValueAt(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Boolean) getValueAt(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (String) getValueAt(columnIndex, String.class);
    }

    private Object getValueAt(int columnIndex, Class<?> valueType) {
        if (types.get(columnIndex) != valueType) {
            throw new ColumnFormatException("Types mismatched");
        }
        return values.get(columnIndex);
    }
}
