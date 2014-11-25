package ru.fizteh.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Интерфейс для подсчёта слов в файлах.
 */
public interface WordCounter {

    /**
     * Выполняет подсчёт слов в указанных файлах.
     *
     * Словом является непрерывная последовательность из букв, цифр, возможно, разделённая одним дефисом.
     *
     * @param inputFiles Список входных файлов. Не должен быть пустым.
     * @param output     Поток, в который выводится результат подсчёта слов. Должен закрываться снаружи метода.
     *                   Формат вывода: название файла (без пути), двоеточие, перенос строки, далее слово, пробел,
     *                   количество вхождений и перенос строки. Для агрегированной статистики название файла не
     *                   выводится.
     *                   Если входной файл не найден, то выводится строка <pre>file not found</pre>.
     *                   Если входной файл недоступен, то выводится строка <pre>file not available</pre>.
     * @param aggregate  Нужно ли агрегировать статистику по всем файлам.
     *
     * @throws java.lang.IllegalArgumentException Если переданы некорректные аргументы.
     * @throws java.io.IOException В случае ошибок ввода-вывода.
     */
    void count(List<File> inputFiles, OutputStream output, boolean aggregate) throws IOException;
}
