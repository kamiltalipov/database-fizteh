package ru.fizteh.fivt.students.kamilTalipov.database.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class XMLProxyLoggerFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer must be not null");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("Implementation must be not null");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class must be not null");
        }

        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Class is not an interface");
        }

        if (!(interfaceClass.isInstance(implementation))) {
            throw new IllegalArgumentException("Object does not implement interface");
        }

        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                                        new Class[]{interfaceClass},
                                        new XMLProxyLogger(writer, implementation));
    }
}
