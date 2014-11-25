package ru.fizteh.fivt.students.kamilTalipov.database.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XMLProxyLogger implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;

    public XMLProxyLogger(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = null;
        Throwable thrown = null;

        try {
            returnValue = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            thrown = e.getTargetException();
        }

        if (method.getDeclaringClass() != Object.class) {
            try {
                XMLFormatter formatter = new XMLFormatter();
                formatter.writeMethodCallLog(method, args, implementation, thrown, returnValue);
                writer.write(formatter.toString() + System.lineSeparator());
            } catch (Throwable e) {
                if (thrown != null) {
                    thrown.addSuppressed(e);
                }
            }
        }

        if (thrown != null) {
            throw thrown;
        }

        return returnValue;
    }
}
