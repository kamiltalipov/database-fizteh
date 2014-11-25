package ru.fizteh.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Интерфейс для поиска строк, удовлетворяющих регулярному выражению.
 */
public interface Grep {

    /**
     * Выполняет поиск строк в указанных файлах.
     *
     * @param inputFiles Список входных файлов. Не должен быть пустым.
     * @param output     Поток, в который выводится результат поиска. Должен закрываться снаружи метода.
     *                   Формат вывода: название файла (без пути), двоеточие, перенос строки, далее все найденные
     *                   строки. Если строк не найдено, выводится строка <pre>no matches</pre>.
     *                   Если входной файл не найден, то выводится строка <pre>file not found</pre>.
     *                   Если входной файл недоступен, то выводится строка <pre>file not available</pre>.
     * @param inverse    Если <code>true</code>, то в выходной поток направляются строки, которые не удовлетворяют
     *                   регулярному выражению.
     *
     * @throws java.lang.IllegalArgumentException Если переданы некорректные аргументы.
     * @throws java.io.IOException В случае ошибок ввода-вывода.
     */
    void find(List<File> inputFiles, OutputStream output, boolean inverse) throws IOException;

    /**
     * Выполняет подсчёт строк в указанных файлах.
     *
     * @param inputFiles Список входных файлов. Не должен быть пустым.
     * @param inverse    Если <code>true</code>, то считаются строки, которые не удовлетворяют
     *                   регулярному выражению.
     * @return Количество найденных строк.
     *
     * @throws java.lang.IllegalArgumentException Если переданы некорректные аргументы.
     * @throws java.io.IOException В случае ошибок ввода-вывода.
     */
    int count(List<File> inputFiles, boolean inverse) throws IOException;
}
