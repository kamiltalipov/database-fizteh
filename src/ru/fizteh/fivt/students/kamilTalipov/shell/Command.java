package ru.fizteh.fivt.students.kamilTalipov.shell;

public interface Command {
    String getName();
    int getNumberOfArguments();
    boolean equalName(String name);

    void run(Shell shell, String[] args) throws IllegalArgumentException;
}
