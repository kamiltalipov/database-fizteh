package ru.fizteh.file;

/**
 * Фабрика для создания поиска строк в файлах.
 *
 * Реализация фабрики должна иметь публичный конструктор без параметров.
 */
public interface GrepFactory {

    /**
     * Создаёт экземпляр класса для поиска строк в файлах.
     *
     * @param pattern Регулярное выражение для поиска.
     * @return Объект для поиска строк.
     *
     * @throws java.lang.IllegalArgumentException Если указан некорректное регулярное выражение.
     */
    Grep create(String pattern);
}
