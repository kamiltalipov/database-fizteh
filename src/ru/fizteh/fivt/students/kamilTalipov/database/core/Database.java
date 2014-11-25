package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.text.ParseException;

public interface Database {
    Storeable put(String key, String value) throws ParseException;

    Storeable get(String key);

    Storeable remove(String key);

    int size();

    String serialize(Storeable storeable);
}
