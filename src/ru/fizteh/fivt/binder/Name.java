package ru.fizteh.binder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Переопределяет имя поля для сериализации/десериализации.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    /**
     * Имя, которое надо использовать для поля при сериализации/десериализации.
     */
    String value();
}
