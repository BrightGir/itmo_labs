package ru.bright.managers;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.bright.model.Flat;
import ru.bright.util.Console;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Класс для управления операциями с файлами
 * Отвечает за чтение и запись коллекции в JSON файл
 */
public class FileManager {

    /**
     * Объект objectMapper для сериализации/десериализации JSON
     */
    private ObjectMapper objectMapper;

    /**
     * Путь к файлу
     */
    private String filePath;

    /**
     * Используемая консоль для вывода ошибок
     */
    private Console console;


    public FileManager(Console console, String filePath) {
        this.filePath = filePath;
        this.console = console;

        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Считывает все строки из файла
     * @return список строк
     */
    public List<String> readLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line = reader.readLine();
            while(line != null && !line.isEmpty()) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (NoSuchFileException e) {
            console.printErr("Файл не найден");
            return null;
        } catch (AccessDeniedException e) {
            console.printErr("Нет прав доступа на чтение файла");
            return null;
        } catch (Exception e) {
            console.printErr("Неизвестная ошибка");
            return null;
        }
        return lines;
    }

    /**
     * Читает коллекцию из файла
     * @return прочитанная коллекция
     */
    public HashSet<Flat> readJsonCollection() {
        HashSet<Flat> loadedCollection;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            loadedCollection = objectMapper.readValue(
                    reader,
                    typeFactory.constructCollectionType(HashSet.class, Flat.class)
            );
            if(loadedCollection == null) {
                loadedCollection = new HashSet<>();
            }
            return loadedCollection;
        } catch (NoSuchFileException e) {
            console.printErr("Файл не найден");
            return null;
        } catch (JsonParseException | MismatchedInputException e) {
            console.printErr("Ошибка при чтении Json либо файл пуст");
            return null;
        } catch (AccessDeniedException e) {
            console.printErr("Нет прав доступа на чтение файла");
            return null;
        } catch (Exception e) {
            console.printErr("Неизвестная ошибка загрузки коллекции из файла");
            return null;
        }
    }

    /**
     * Записывает коллекцию в файл
     * @param collection коллекция для записи
     * @throws IOException
     */
    public boolean writeJsonCollection(HashSet<Flat> collection) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            if(collection.isEmpty()) {
                writer.write("");
            } else {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, collection);
            }
            return true;
        } catch (IOException e) {
            console.printErr("Ошибка при записи в файл");
        }
        return false;
    }

}