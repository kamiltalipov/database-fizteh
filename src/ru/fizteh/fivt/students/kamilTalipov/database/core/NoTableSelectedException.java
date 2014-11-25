package ru.fizteh.fivt.students.kamilTalipov.database.core;

public class NoTableSelectedException extends RuntimeException {
    public NoTableSelectedException(String message) {
        super(message);
    }
}
