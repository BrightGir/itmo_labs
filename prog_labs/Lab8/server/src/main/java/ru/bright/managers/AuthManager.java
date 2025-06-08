package ru.bright.managers;

import ru.bright.model.Flat;
import ru.bright.server.Server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class AuthManager {

    private Server server;
    private Map<String, String> users;

    public AuthManager(Server server) {
        this.server = server;
        this.users = new HashMap<>();
    }

    public void loadAllUsers() {
        server.getLogger().log(Level.INFO, "Loading users from DB...");
        Set<Flat> flats = new HashSet<>();
        String sql = "SELECT login, password_hash FROM users";

        try (Connection connection = server.getDatabaseManger().getConnection(); // Используй свой dbManager
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String login = rs.getString("login");
                String pass_hash = rs.getString("password_hash");
                users.put(login, pass_hash);
            }
        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE, "Users was not loaded", e);
        }
        server.getLogger().log(Level.INFO, "Users has been loaded");
    }

    public void init() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "login VARCHAR(255) UNIQUE NOT NULL PRIMARY KEY, " +
                "password_hash VARCHAR(255) NOT NULL" +
                ");";

        try (Connection connection = server.getDatabaseManger().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            server.getLogger().log(Level.INFO,"User table was initialized");

        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE,"User table creation error", e);
            server.shutdown();
        }
        loadAllUsers();
    }

    public boolean hasLogin(String login) {
        /*
        if (login == null || login.trim().isEmpty()) {
            return true;
        }
        String sql = "SELECT login FROM users WHERE login = ?";
        try (Connection connection = server.getDatabaseManger().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE,"Check value in database error", e);
            throw e;
        }
        */

        if(users.containsKey(login)) {
            return true;
        }
        return false;
    }


    public boolean register(String login, String password) throws SQLException {
        String passwordHash = hashPasswordMD2(password);
        if (passwordHash == null || login == null || login.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";

        try (Connection connection = server.getDatabaseManger().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login.trim());
            preparedStatement.setString(2, passwordHash);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                users.put(login, passwordHash);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE,"Push user to database error", e);
            throw e;
        }
    }

    public boolean verify(String login, String password) {
        String passwordHash = hashPasswordMD2(password);
        if(users.containsKey(login) && users.get(login).equals(passwordHash)) {
            return true;
        }
        return false;
        /*
        if (passwordHash == null || login == null || login.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT login FROM users WHERE login = ? AND password_hash = ?";

        try (Connection connection = server.getDatabaseManger().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, login.trim());
            preparedStatement.setString(2, passwordHash);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return  resultSet.next();
            }

        } catch (SQLException e) {
            server.getLogger().log(Level.SEVERE,"Verify user in database error", e);
            throw e;
        }
         */
    }

    private String hashPasswordMD2(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            server.getLogger().log(Level.SEVERE,"MD2 Algorithm dont found!", e);
            server.shutdown();
        }
        return null;
    }
}
