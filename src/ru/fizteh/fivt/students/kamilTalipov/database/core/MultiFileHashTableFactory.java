package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MultiFileHashTableFactory implements TableProviderFactory, AutoCloseable {
    private final ArrayList<MultiFileHashTableProvider> providers;

    private volatile boolean isClosed = false;

    public MultiFileHashTableFactory() {
        providers = new ArrayList<>();
    }

    @Override
    public MultiFileHashTableProvider create(String dir) throws IllegalArgumentException, IOException {
        checkState();
        if (dir == null) {
            throw new IllegalArgumentException("Directory path must be not null");
        }

        try {
            synchronized (providers) {
                providers.add(new MultiFileHashTableProvider(dir));
                return providers.get(providers.size() - 1);
            }
        } catch (DatabaseException e) {
            throw new IllegalArgumentException("Database error", e);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found", e);
        }
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }

        isClosed = true;
        synchronized (providers) {
            for (MultiFileHashTableProvider provider : providers) {
                provider.close();
            }
        }
    }

    private void checkState() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException("Factory is closed");
        }
    }
}
