package ru.fizteh.fivt.students.kamilTalipov.database.test;

import org.junit.*;

import static org.junit.Assert.*;

import ru.fizteh.fivt.students.kamilTalipov.database.proxy.XMLProxyLoggerFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxyTester {
    public XMLProxyLoggerFactory loggerFactory;
    public StringWriter writer;

    @Before
    public void before() {
        loggerFactory = new XMLProxyLoggerFactory();
        writer = new StringWriter();
    }

    @Test
    public void objectMethodNotLogTest() {
        List<Object> list = (List<Object>) loggerFactory.wrap(writer, new ArrayList<>(), List.class);

        Assert.assertEquals("[]", list.toString());
        Assert.assertEquals("", writer.toString());

        list.hashCode();
        Assert.assertEquals("", writer.toString());

        list.getClass();
        Assert.assertEquals("", writer.toString());

        Assert.assertEquals(false, list.equals(null));
        Assert.assertEquals("", writer.toString());
    }

    @Test(timeout = 200)
    public void cyclicReferenceTest() {
        List<Object> cyclicList = new ArrayList<>();
        cyclicList.add(cyclicList);

        List<Object> proxyList = (List<Object>) loggerFactory.wrap(writer, new ArrayList<>(), List.class);
        Assert.assertEquals(-1, proxyList.indexOf(cyclicList));
        Assert.assertEquals("name=\"indexOf\" class=\"java.util.ArrayList\">"
                + "<arguments><argument><list><value><list><value>cyclic</value>"
                + "</list></value></list></argument></arguments><return>-1</return>",
                getMessageFromLog(writer.toString()));

    }

    @Test
    public void sameObjectInArgumentsTest() {
        List<Object> list = new ArrayList<>();
        List<Integer> sameObject = Arrays.asList(5, 10);

        List<List<Integer>> arguments = Arrays.asList(sameObject, sameObject, sameObject);
        List<Object> proxyList = (List<Object>) loggerFactory.wrap(writer, new ArrayList<>(), List.class);
        Assert.assertEquals(-1, proxyList.indexOf(arguments));
        Assert.assertEquals("name=\"indexOf\" class=\"java.util.ArrayList\"><arguments><argument>"
                + "<list><value><list><value>5</value><value>10</value></list></value>"
                + "<value><list><value>5</value><value>10</value></list></value><value>"
                + "<list><value>5</value><value>10</value></list></value></list></argument>"
                + "</arguments><return>-1</return>",
                getMessageFromLog(writer.toString()));
    }

    @Test
    public void sameExceptionTest() {
        ExceptionGenerator generator = (ExceptionGenerator) loggerFactory.wrap(writer,
                                                                new ExceptionGeneratorImpl(),
                                                                ExceptionGenerator.class);
        try {
            generator.throwIllegalArgumentException();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //normal way
        }
        Assert.assertEquals("name=\"throwIllegalArgumentException\" class=\"ru.fizteh.fivt.students"
                + ".kamilTalipov.database.test"
                + ".ProxyTester$ExceptionGeneratorImpl\"><arguments>"
                + "</arguments><thrown>java.lang.IllegalArgumentException</thrown>",
                getMessageFromLog(writer.toString()));
    }

    @Test
    public void listIntegerAddLogTest() {
        List<Integer> list = (List<Integer>) loggerFactory.wrap(writer, new ArrayList(), List.class);
        list.add(1);
        Assert.assertEquals("name=\"add\" class=\"java.util.ArrayList\">"
                + "<arguments><argument>1</argument></arguments><return>true</return>",
                getMessageFromLog(writer.toString()));
    }

    @Test
    public void nullLogTest() {
        List<Integer> list = (List<Integer>) loggerFactory.wrap(writer, new ArrayList(), List.class);
        list.add(null);
        Assert.assertEquals("name=\"add\" class=\"java.util.ArrayList\">"
                + "<arguments><argument><null></null></argument></arguments><return>true</return>",
                getMessageFromLog(writer.toString()));
    }

    @Test
    public void emptyArgsMethodLogTest() {
        List<Integer> list = (List<Integer>) loggerFactory.wrap(writer, new ArrayList(), List.class);
        Assert.assertEquals(list.size(), 0);
        Assert.assertEquals("name=\"size\" class=\"java.util.ArrayList\">"
                + "<arguments></arguments><return>0</return>",
                getMessageFromLog(writer.toString()));
    }

    private static String getMessageFromLog(String log) {
        String[] strings = log.split("\\s");
        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < strings.length; ++i) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(" ");
            }
        }
        builder.delete(builder.length() - 9, builder.length());

        return builder.toString();
    }

    public interface ExceptionGenerator {
        void throwIllegalArgumentException();
    }

    public class ExceptionGeneratorImpl implements ExceptionGenerator {
        @Override
        public void throwIllegalArgumentException() {
            throw new IllegalArgumentException();
        }
    }
}
