package ru.fizteh.fivt.students.kamilTalipov.database.test;

import org.junit.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.DatabaseException;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.core.TableRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreableTester {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public MultiFileHashTableProvider provider;
    public MultiFileHashTable table;

    @Before
    public void initTable() throws IOException, DatabaseException {
        provider = new MultiFileHashTableProvider(folder.getRoot().getAbsolutePath());

        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        table = provider.createTable("Test", types);
    }

    @Test
    public void putGetTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        table.put("test", storeable);
        Assert.assertEquals(table.get("test"), storeable);
    }

    @Test
    public void createForTest() {
        Storeable storeable = provider.createFor(table);
        Assert.assertEquals(storeable.getColumnAt(0), null);
        Assert.assertEquals(storeable.getColumnAt(1), null);
    }

    @Test
    public void createForTest2() {
        Storeable storeable = provider.createFor(table, Arrays.asList(54, "tgeg"));
        Assert.assertEquals(storeable.getColumnAt(0).getClass(), Integer.class);
        Assert.assertEquals(storeable.getColumnAt(1).getClass(), String.class);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIncorrectColumnTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(2, "two"));
        storeable.getColumnAt(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStoreableNullTypesTest() {
        Storeable storeable = new TableRow(table, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void setIncorrectTypeValue() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(0, 3.4);
    }

    @Test
    public void getIntStringTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        Assert.assertEquals(storeable.getIntAt(0), Integer.valueOf(3));
        Assert.assertEquals(storeable.getStringAt(1), "three");
    }

    @Test
    public void getAllSupportedTypesTest() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Long.class);
        types.add(Byte.class);
        types.add(Float.class);
        types.add(Double.class);
        types.add(Boolean.class);
        types.add(String.class);

        MultiFileHashTable table2 = provider.createTable("TypeTest", types);

        Storeable storeable = new TableRow(table2, Arrays.asList(12, 34L, Byte.valueOf("3"),
                                                                12.4f, 55.2, true, "big"));
        Assert.assertEquals(storeable.getIntAt(0), Integer.valueOf(12));
        Assert.assertEquals(storeable.getLongAt(1), Long.valueOf(34L));
        Assert.assertEquals(storeable.getByteAt(2), Byte.valueOf("3"));
        Assert.assertEquals(storeable.getFloatAt(3), Float.valueOf(12.4f));
        Assert.assertEquals(storeable.getDoubleAt(4), Double.valueOf(55.2));
        Assert.assertEquals(storeable.getBooleanAt(5), true);
        Assert.assertEquals(storeable.getStringAt(6), "big");
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesGetShouldFailedTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesGetShouldFailedTest2() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getLongAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesGetShouldFailedTest3() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getByteAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesGetShouldFailedTest4() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getFloatAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesGetShouldFailedTest5() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getDoubleAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesGetShouldFailedTest6() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getBooleanAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesGetShouldFailedTest7() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getStringAt(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutGetIntTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getIntAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutGetLongTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getLongAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutGetByteTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getByteAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutGetFloatTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getFloatAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutGetDoubleTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getDoubleAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutBooleanByteTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getBooleanAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexOutGetStringTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(3, "three"));
        storeable.getStringAt(10);
    }
}
