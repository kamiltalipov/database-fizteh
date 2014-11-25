package ru.fizteh.binder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Представляет интерфейс для выполнения сериализации/десериализации объектов.
 *
 * @param <T> Тип для сериализации/десериализации.
 */
public interface Binder<T> {

    /**
     * Выполняет десериализацию из <code>input</code> объекта.
     *
     * @param input Поток, в котором записан сериализованный объект.
     * @return Десериализованный объект.
     *
     * @throws java.io.IOException В случае возникновения ошибок ввода/вывода.
     * @throws IllegalArgumentException Если <code>input</code> является <code>null</code>.
     */
    T deserialize(InputStream input) throws IOException;

    /**
     * Выполняет сериализацию объекта.
     *
     * @param value  Объект для сериализации.
     * @param output Поток, в который необходимо записать объект.
     *
     * @throws java.io.IOException В случае возникновения ошибок ввода/вывода.
     * @throws IllegalArgumentException Если <code>input</code> или <code>output</code> являются <code>null</code>.
     * @throws IllegalStateException Если обнаружена циклическая ссылка.
     */
    void serialize(T value, OutputStream output) throws IOException;
}
