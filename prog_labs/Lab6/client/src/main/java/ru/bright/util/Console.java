package ru.bright.util;

import java.io.IOException;

/**
 * Интерфейс для работы с консолью
 */
public interface Console {

    /**
     * Выводит текст без перевода строки
     * @param text
     */
    void print(String text);

    /**
     * Выводит текст с переводом строки
     * @param text
     */
    void println(String text);

    /**
     * Выводит текст об ошибке
     * @param text
     */
    void printErr(String text);

    /**
     * Читает строку из консоли
     * @return Считанная строка
     * @throws IOException
     */
    String readLine() throws IOException;

}
