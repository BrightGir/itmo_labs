package ru.bright.managers;

import ru.bright.server.Server;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseManger {

    private Server server;
    private Connection connection;
  //  private String DB_URL = "jdbc:postgresql://pg:5432/studs";
   // private String DB_URL = "jdbc:postgresql://pg:5432/studs";
    private String USER, PASS, DB_URL;

    public DatabaseManger(Server server, String db_url, String user, String pass) {
        this.server = server;
        this.USER = user;
        this.PASS = pass;
        this.DB_URL = db_url;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL driver not found");
            System.exit(1);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            server.getLogger().log(Level.INFO, "Connection with database server has been opened.");
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                server.getLogger().log(Level.INFO, "Connection with database server was closed.");
                connection = null;
            } catch (SQLException e) {
                server.getLogger().log(Level.SEVERE, "Error while connection closing: ", e);
            }
        }
    }
}
