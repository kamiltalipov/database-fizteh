package ru.fizteh.fivt.students.kamilTalipov.database.servlet;

import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class TransactionManager  {
    private HashMap<String, TransactionData> transactions;

    private Queue<Integer> availableId;
    private final int idLength;

    private final ReadWriteLock lock;

    public TransactionManager(int idLength) {
        this.idLength = idLength;
        transactions = new HashMap<>();
        availableId = new LinkedList<>();
        lock = new ReentrantReadWriteLock();

        int maxId = getMaxId();
        for (int id = 0; id <= maxId; ++id) {
            availableId.add(id);
        }
    }

    public int getMaxId() {
        int maxId = 1;
        for (int i = 0; i < idLength; ++i) {
            maxId *= 10;
        }
        return maxId - 1;
    }

    public String startTransaction(MultiFileHashTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Table should be not null");
        }

        lock.writeLock().lock();
        try {
            if (availableId.isEmpty()) {
                throw new IllegalStateException("Too many transaction is started");
            }

            int transactionId = availableId.remove();
            String formatString = "%0" + idLength + "d";
            String id = String.format(formatString, transactionId);
            transactions.put(id, new TransactionData(table));
            return id;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void stopTransaction(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction id should be not null");
        }
        if (id.length() != idLength) {
            throw new IllegalArgumentException("Invalid length of transaction id");
        }
        for (int i = 0; i < id.length(); ++i) {
            if (!Character.isDigit(id.charAt(i)))  {
                throw new IllegalArgumentException("Transaction id should contain only digits");
            }
        }

        lock.writeLock().lock();
        try {
            if (transactions.remove(id) == null) {
                throw new IllegalArgumentException("Transaction does not exist");
            }
            availableId.add(Integer.valueOf(id));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public TransactionData getTransaction(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction id should be not null");
        }
        if (id.length() != idLength) {
            throw new IllegalArgumentException("Invalid length of transaction id");
        }
        for (int i = 0; i < id.length(); ++i) {
            if (!Character.isDigit(id.charAt(i)))  {
                throw new IllegalArgumentException("Transaction id should contain only digits");
            }
        }

        lock.readLock().lock();
        try {
            TransactionData transaction = transactions.get(id);
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction does not exist");
            }
            return transaction;
        } finally {
            lock.readLock().unlock();
        }
    }

    public TransactionHandler createHandlerFor(MultiFileHashTable table) {
        return new TransactionHandler(table, this);
    }
}
