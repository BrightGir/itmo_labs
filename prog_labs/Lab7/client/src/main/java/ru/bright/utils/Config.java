package ru.bright.utils;



import ru.bright.FileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public class Config {

    private static final String CONFIG_FILE_NAME = "app_settings.properties";
    public static final String KEY_LANGUAGE = "application.language";
    private static final String DEFAULT_LANGUAGE_CODE = "ru";

    private FileManager fileManager;
    private Properties appProperties;
    private Path configFilePath;

    public Config() {
        this.fileManager = new FileManager();
        this.configFilePath = FileManager.getConfigFilePath(CONFIG_FILE_NAME);
        init();
    }

    private void init() {
        try {
            this.appProperties = fileManager.readProperties(configFilePath);
        } catch (IOException e) {
            System.err.println("Критическая ошибка при чтении конфигурационного файла: " + configFilePath.toAbsolutePath() + " - " + e.getMessage());
            System.err.println("Будут использованы значения по умолчанию.");
            this.appProperties = new Properties();
        }

        ensureDefaultSetting(KEY_LANGUAGE, DEFAULT_LANGUAGE_CODE);
    }

    private void ensureDefaultSetting(String key, String defaultValue) {
        if (!appProperties.containsKey(key)) {
            appProperties.setProperty(key, defaultValue);
        }
    }

    private void saveCurrentProperties() {
        try {
            fileManager.writeProperties(appProperties, configFilePath, null);
            System.out.println("Конфигурация сохранена в: " + configFilePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении конфигурационного файла: " + configFilePath.toAbsolutePath() + " - " + e.getMessage());
        }
    }

    public String getSetting(String key, String defaultValue) {
        if (appProperties == null) return defaultValue;
        return appProperties.getProperty(key, defaultValue);
    }


    public void setSetting(String key, String value) {
        if (appProperties == null) {
            appProperties = new Properties();
        }
        appProperties.setProperty(key, value);
        saveCurrentProperties();
    }

    public Locale getDefaultLanguage() {
        return new Locale(DEFAULT_LANGUAGE_CODE);
    }

    public Locale getApplicationLocale() {
        String langCode = getSetting(KEY_LANGUAGE, DEFAULT_LANGUAGE_CODE);
        return new Locale(langCode);
    }

    public void setApplicationLocale(Locale locale) {
        setSetting(KEY_LANGUAGE, locale.getLanguage());
    }
}