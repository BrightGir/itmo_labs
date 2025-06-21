package ru.bright.managers;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.bright.server.Server;
import ru.bright.model.Flat;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Класс для управления операциями с файлами
 * Отвечает за чтение и запись коллекции в JSON файл
 */
public class CollectionDatabaseManager {

    /**
     * Объект objectMapper для сериализации/десериализации JSON
     */
    private ObjectMapper objectMapper;

    /**
     * Путь к файлу
     */
    private String filePath;

    /**
     * Используемый сервер для вывода ошибок
     */
    private Server server;


    public CollectionDatabaseManager(Server server, String filePath) {
        this.filePath = filePath;
        this.server = server;

        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    protected void initCollectionTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS flats (\n" +
                "    id BIGSERIAL NOT NULL PRIMARY KEY ,\n" +
                "\n" +
                "    owner_login VARCHAR(255) NOT NULL,\n" +
                "\n" +
                "    flat_data JSONB NOT NULL,\n" +
                "\n" +
                "    FOREIGN KEY (owner_login) REFERENCES users(login)\n" +
                "        ON DELETE CASCADE\n" +
                "        ON UPDATE CASCADE\n" +
                ");";

        try(Connection connection = server.getDatabaseManger().getConnection();
            Statement statement = connection.createStatement();) {
            statement.execute(sql);
            server.getLogger().log(Level.INFO,"Collection table was initialized");
        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE,"Collection table initialization failed",e);
        }
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
            server.getLogger().log(Level.SEVERE, "File not found", e);
            server.shutdown();
            return null;
        } catch (AccessDeniedException e) {
            server.getLogger().log(Level.SEVERE, "Access denied", e);
            server.shutdown();
            return null;
        } catch (Exception e) {
            server.getLogger().log(Level.SEVERE, "Unknown error", e);
            server.shutdown();
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
        } catch (NoSuchFileException | NullPointerException e) {
            server.getLogger().log(Level.SEVERE, "File not found", e);
            server.shutdown();
            return null;
        } catch (JsonParseException | MismatchedInputException e) {
            server.getLogger().log(Level.SEVERE, "Json reading error or file is empty", e);
            server.shutdown();
            return null;
        } catch (AccessDeniedException e) {
            server.getLogger().log(Level.SEVERE, "Access denied", e);
            server.shutdown();
            return null;
        } catch (Exception e) {
            server.getLogger().log(Level.SEVERE, "Unknown error", e);
            server.shutdown();
            return null;
        }
    }


    private String serializeFlatToJson(Flat flat) {
        try {
            return objectMapper.writeValueAsString(flat);
        } catch (JsonProcessingException e) {
            server.getLogger().log(Level.SEVERE, "Error while serializing Flat to JSON", e);
            server.shutdown();
        }
        return null;
    }

    public Set<Flat> loadAllFlatsDB() {
        server.getLogger().log(Level.INFO, "Loading collection from DB...");
        Set<Flat> flats = new HashSet<>();
        String sql = "SELECT id, owner_login, flat_data FROM flats";

        try (Connection connection = server.getDatabaseManger().getConnection(); // Используй свой dbManager
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Flat flat = null;
                long id = 0;
                try {
                    id = rs.getLong("id");
                    String ownerLogin = rs.getString("owner_login");
                    String flatJsonString = rs.getString("flat_data");
                    flat = objectMapper.readValue(flatJsonString, Flat.class);
                    flat.setId(id);
                    flat.setOwnerLogin(ownerLogin);
                    flats.add(flat);
                } catch (Exception e) {
                    server.getLogger().log(Level.SEVERE, "Error while deserializing Flat, id = " + id, e);
                }
            }
        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE, "Collection was not loaded", e);
        }
        server.getLogger().log(Level.INFO, "Collection has been loaded");
        return flats;
    }

    public Long addFlatJson(Flat flat) throws SQLException {
        String flatJsonString = serializeFlatToJson(flat);
        String sql = "INSERT INTO flats (owner_login, flat_data) VALUES (?, ?::jsonb) RETURNING id";
        Long generatedId = null;

        try (Connection connection = server.getDatabaseManger().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, flat.getOwnerLogin());
            ps.setString(2, flatJsonString);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    generatedId = rs.getLong(1);
                    flat.setId(generatedId);
                    server.getLogger().log(Level.INFO, "New flat with id " + generatedId + " has added");
                } else {
                    server.getLogger().log(Level.SEVERE, "No generated ID for new flat");
                    throw new SQLException("ID is not exists");
                }
            }
        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE, "Error while adding flat", e);
            throw e;
        }
        return generatedId;
    }

    public boolean update(Flat flat) throws SQLException {
      //  server.getLogger().log(Level.INFO, "Flat to update = " + flat.toString());
        String sql = "UPDATE flats SET flat_data = ?::jsonb WHERE id = ?";
        server.getLogger().log(Level.INFO, "Updating flat with id " + serializeFlatToJson(flat));
        try (Connection connection = server.getDatabaseManger().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serializeFlatToJson(flat)); 
            ps.setLong(2, flat.getId()); 
            int rows = ps.executeUpdate();
            if (rows > 0) {
                server.getLogger().log(Level.INFO, "Flat with ID was updated: " + flat.getName());
            }
        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE, "Error while updating flat with ID = " + flat.getId(), e);
            throw e;
        }
        return true;
    }


    public boolean deleteFlatById(long id) throws SQLException {
        String sql = "DELETE FROM flats WHERE id = ?";
        try (Connection connection = server.getDatabaseManger().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id); 

            int rows = ps.executeUpdate();

            if (rows > 0) {
                server.getLogger().log(Level.INFO, "Flat with ID was deleted: " + id);
            }
        } catch (SQLException e) {
                server.getLogger().log(Level.SEVERE, "Error while deleting flat with ID = " + id, e);
                throw e;
        }
        return true;
    }
}
