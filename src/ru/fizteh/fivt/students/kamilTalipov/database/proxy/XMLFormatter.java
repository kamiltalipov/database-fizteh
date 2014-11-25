package ru.fizteh.fivt.students.kamilTalipov.database.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

public class XMLFormatter {
    private final StringWriter stringWriter;
    private final XMLStreamWriter xmlWriter;
    private final IdentityHashMap<Object, Boolean> identityHashMap;

    public XMLFormatter() throws XMLStreamException {
        stringWriter = new StringWriter();
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        xmlWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
        identityHashMap = new IdentityHashMap<>();
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }

    public void writeMethodCallLog(Method method, Object[] args, Object implementation,
                                   Throwable thrown, Object returnValue) throws IOException {
        try {
            xmlWriter.writeStartElement("invoke");
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }

        writeTimestamp();
        writeMethod(method);
        writeClass(implementation.getClass());

        writeArguments(args);
        if (thrown != null) {
            writeThrown(thrown);
        } else {
            if (method.getReturnType() != Void.TYPE) {
                writeReturnValue(returnValue);
            }
        }

        try {
            xmlWriter.writeEndElement();
            xmlWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeTimestamp() throws IOException {
        try {
            xmlWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeClass(Class<?> clazz) throws IOException {
        try {
            xmlWriter.writeAttribute("class", clazz.getName());
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeMethod(Method method) throws IOException {
        try {
            xmlWriter.writeAttribute("name", method.getName());
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeArguments(Object[] args) throws IOException {
        try {
            xmlWriter.writeStartElement("arguments");
            if (args != null) {
                for (Object object : args) {
                    xmlWriter.writeStartElement("argument");

                    if (object == null) {
                        writeNull();
                    } else if (object instanceof Iterable) {
                        identityHashMap.clear();
                        identityHashMap.put(object, true);
                        writeItereable((Iterable) object);
                    } else {
                        writeObject(object);
                    }

                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeReturnValue(Object returnValue) throws IOException {
        try {
            xmlWriter.writeStartElement("return");
            if (returnValue == null) {
                writeNull();
            } else {
                xmlWriter.writeCharacters(returnValue.toString());
            }
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    public void writeThrown(Throwable throwable) throws IOException {
        try {
            xmlWriter.writeStartElement("thrown");
            xmlWriter.writeCharacters(throwable.toString());
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("XML write error", e);
        }
    }

    private void writeNull() throws XMLStreamException {
        xmlWriter.writeStartElement("null");
        xmlWriter.writeEndElement();
    }

    private void writeItereable(Iterable iterable) throws XMLStreamException {
        xmlWriter.writeStartElement("list");

        for (Object object : iterable) {
            xmlWriter.writeStartElement("value");

            if (object == null) {
                writeNull();
            } else if (object instanceof Iterable) {
                if (identityHashMap.put(object, true) != null) {
                    xmlWriter.writeCharacters("cyclic");
                } else {
                    writeItereable((Iterable) object);
                    identityHashMap.remove(object);
                }
            } else {
                identityHashMap.put(object, true);
                writeObject(object);
            }

            xmlWriter.writeEndElement();
        }

        xmlWriter.writeEndElement();
    }

    private void writeObject(Object object) throws XMLStreamException {
        xmlWriter.writeCharacters(object.toString());
    }
}
