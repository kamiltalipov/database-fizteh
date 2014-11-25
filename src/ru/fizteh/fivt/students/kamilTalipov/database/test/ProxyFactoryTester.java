package ru.fizteh.fivt.students.kamilTalipov.database.test;

import org.junit.*;
import ru.fizteh.fivt.students.kamilTalipov.database.proxy.XMLProxyLoggerFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ProxyFactoryTester {
    public XMLProxyLoggerFactory factory;

    @Before
    public void before() {
        factory = new XMLProxyLoggerFactory();
    }

    @Test
    public void normalWrap() {
        StringWriter writer = new StringWriter();
        factory.wrap(writer, new ArrayList<Integer>(), List.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullWriterWrapTest() {
        factory.wrap(null, new ArrayList<Integer>(), List.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullImplementationWrapTest() {
        StringWriter writer = new StringWriter();
        factory.wrap(writer, null, List.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInterfaceWrapTest() {
        StringWriter writer = new StringWriter();
        factory.wrap(writer, new ArrayList<Integer>(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notInterfaceWrapTest() {
        StringWriter writer = new StringWriter();
        factory.wrap(writer, new ArrayList<Integer>(), ArrayList.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notInstanceWrapTest() {
        StringWriter writer = new StringWriter();
        factory.wrap(writer, new ArrayList<Integer>(), AutoCloseable.class);
    }
}
