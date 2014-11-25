package ru.fizteh.binder;

/**
 * Фабрика для создания экземпляров {@link ru.fizteh.fivt.binder.Binder}.
 */
public interface BinderFactory {

    /**
     * Создаёт экземпляр {@link ru.fizteh.fivt.binder.Binder} для указанного типа.
     *
     * @param clazz Класс, описывающий тип для сериализации.
     * @param <T>   Тип для сериализации.
     * @return Экземпляр {@link ru.fizteh.fivt.binder.Binder}.
     *
     * @throws IllegalArgumentException Если <code>clazz</code> имеет недопустимое значение, либо сериализация/десериализация
     * указанного типа невозможна (нет конструктора по умолчанию, содержит несериализуемые типы).
     */
    <T> Binder<T> create(Class<T> clazz);
}
