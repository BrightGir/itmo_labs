package ru.bright.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Базовая реализация консоли с потоком вывода System.out и ввода BufferedReader.
 */
public class BasicConsole implements Console {

    private BufferedReader reader;

    public BasicConsole() {

    }

    /**
     * Устанавливает BufferedReader для консоли
     * @param reader
     */
    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void print(String text) {
        System.out.print(text);
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }

    @Override
    public void printErr(String text) {
        System.out.println("Error: " + text);
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }

}
