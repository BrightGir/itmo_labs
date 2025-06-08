package ru.bright;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class FileManager {

    public void writeProperties(Properties properties, Path filePath, String comments) throws IOException {
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        try (OutputStream output = new FileOutputStream(filePath.toFile())) {
            properties.store(output, comments);
        }
    }


    public Properties readProperties(Path filePath) throws IOException {
        Properties properties = new Properties();
        if (Files.exists(filePath)) {
            try (InputStream input = new FileInputStream(filePath.toFile())) {
                properties.load(input);
            }
        } else {
            System.out.println("Файл конфигурации не найден по пути: " + filePath.toAbsolutePath() + ". Будет создан новый или использованы значения по умолчанию.");
        }
        return properties;
    }

    public static Path getConfigFilePath(String fileName) {
        return Paths.get(System.getProperty("user.dir"), fileName);
    }

    public boolean deleteFile(Path filePath) {
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при удалении файла " + filePath + ": " + e.getMessage());
            return false;
        }
    }
}