package ru.fizteh.fivt.students.kamilTalipov.database.test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kamilTalipov.database.core.DatabaseException;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class TableProviderTester {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public MultiFileHashTableProvider provider;

    @Before
    public void initProvider() throws IOException, DatabaseException {
        provider = new MultiFileHashTableProvider(folder.getRoot().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void  illegalInitTest() throws IOException, DatabaseException {
        MultiFileHashTableProvider badProvider = new MultiFileHashTableProvider("gjfdou34923dkfjs");
    }

    @Test
    public void createGetRemoveTest() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Double.class);

        provider.createTable("Test", types);
        Assert.assertNotNull(provider.getTable("Test"));
        provider.removeTable("Test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTest() throws IOException {
        provider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTableTest() {
        provider.getTable("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createIncorrectTableNameTest() {
        provider.getTable("??/?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        provider.removeTable(null);
    }

    @Test
    public void parallelCreateTableTest() throws ExecutionException, InterruptedException {
        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws IOException {
                List<Class<?>> types = new ArrayList<>();
                types.add(Integer.class);
                return provider.createTable("ParallelCreateTest", types) == null;
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        try {
        Future<Boolean> result1 = executor.submit(task);
        Future<Boolean> result2 = executor.submit(task);
        Assert.assertEquals(true, result1.get() || result2.get());
        Assert.assertEquals(false, result1.get() == result2.get());
        } finally {
        executor.shutdown();
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Timeout");
            }
        }
    }

    @Test
    public void parallelCreateTableGetTableTest() throws ExecutionException, InterruptedException {
        Callable<Table> task = new Callable<Table>() {
            @Override
            public Table call() throws IOException {
                List<Class<?>> types = new ArrayList<>();
                types.add(Integer.class);
                Table result = provider.createTable("Table123", types);
                if (result == null) {
                    return provider.getTable("Table123");
                }
                return result;
            }
        };


        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Future<Table> result1 = executor.submit(task);
            Future<Table> result2 = executor.submit(task);
            Assert.assertEquals("Table123", result1.get().getName());
            Assert.assertEquals("Table123", result2.get().getName());
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Timeout");
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void parallelRemoveTest() throws IOException, InterruptedException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        provider.createTable("Table2", types);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                provider.removeTable("Table2");
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.submit(task);
            executor.submit(task);
            executor.submit(task);
            executor.submit(task);

            provider.removeTable("Table2");
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Timeout");
            }
        }
    }

    @Test
    public void manyClosesTest() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        provider.createTable("Table1", types);
        provider.createTable("Table2", types);

        for (int i = 0; i < 5; ++i) {
            provider.close();
        }
    }

    @Test
    public void workAfterCloseTest() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Table table = provider.createTable("Hello", types);

        provider.close();

        try {
            provider.createTable("hello", types);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //normal result
        }

        try {
            provider.removeTable("test");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //normal result
        }

        try {
            provider.getTable("test");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //normal result
        }

        try {
            provider.createFor(table);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //normal result
        }

        try {
            ArrayList<Object> testValues = new ArrayList<>();
            testValues.add(5);
            testValues.add("value");
            provider.createFor(table, testValues);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //normal result
        }
    }
}
