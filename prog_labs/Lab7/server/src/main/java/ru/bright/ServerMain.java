package ru.bright;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import ru.bright.commands.*;
import ru.bright.managers.*;
import ru.bright.server.Server;


import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {

    private static Logger LOGGER = Logger.getLogger(ServerMain.class.getName());
    private static String DB_USER = "db_user";
    private static String DB_PASSWORD = "db_pass";
    private static String SSH_USER = "user";
    private static String SSH_PASSWORD = "password";
    private static String SSH_SERVER = "server";
    private static int SSH_PORT = 1111;
    private static int SSH_DB_PORT = 11;
    private static int LOCAL_PORT = 1111;
    private static String DB_HOST = "jdbc:postgresql://localhost:" + LOCAL_PORT +  "/studs";
    private static Session session;

    public static void main(String[] args) {
        //  BasicConsole console = new BasicConsole();
        String filePath =
                System.getProperty("user.dir") + File.separator + "collection.txt";
        if(args != null && args.length == 1) {
            filePath = System.getenv(args[0]);
        }

        int portNumber = 8080;
        Server server = new Server(portNumber);
        ServerCommandManager commandManager = new ServerCommandManager(server);
        server.setServerCommandManager(commandManager);
        server.setLogger(LOGGER);
        makeSSHtonnel();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeSSHtonnel();
        }));
        server.setDatabaseManager(new DatabaseManger(server,DB_HOST,
                DB_USER,DB_PASSWORD));
        CollectionDatabaseManager fileManager = new CollectionDatabaseManager(server,
                filePath);
        CollectionManager collectionManager = new CollectionManager(server, fileManager);
        server.setCollectionManager(collectionManager);
        pullCommands(server,commandManager,collectionManager);
        AuthManager authManager = new AuthManager(server);
        server.setAuthManager(authManager);
        authManager.init();
        if(collectionManager.loadJsonCollectionFromDatabase()) {
            LOGGER.log(Level.INFO,"Collection from the file was successfully loaded");
        }

        server.startServer();

    }

    private static void closeSSHtonnel() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            LOGGER.log(Level.INFO, "SSH session shutdown.");
        } else {
            LOGGER.log(Level.INFO, "SSH session shutdown error.");
        }
    }

    private static void makeSSHtonnel() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SSH_USER, SSH_SERVER, SSH_PORT);
            session.setPassword(SSH_PASSWORD);


            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            session.setPortForwardingL(LOCAL_PORT, "pg", 5432);
        } catch (JSchException e) {
            getLogger().log(Level.SEVERE,"SSH connection error", e);
            System.exit(1);
        }
      //  JSch
    }

    public static Logger getLogger() {
        return LOGGER;
    }
    /**
     * Добавляет все доступные команды в менеджер команд.
     */
    private static void pullCommands(Server server, ServerCommandManager commandManager, CollectionManager collectionManager) {
        commandManager.registerCommand(new AddCommand(server, collectionManager));
        commandManager.registerCommand(new AddIfMaxCommand(server, collectionManager));
        commandManager.registerCommand(new ClearCommand(server, collectionManager));
        commandManager.registerCommand(new ExecuteScriptCommand(server, commandManager));
        commandManager.registerCommand(new FilterStartsWithNameCommand(server, collectionManager));
        commandManager.registerCommand(new HistoryCommand(server, commandManager));
        commandManager.registerCommand(new InfoCommand(server, collectionManager));
        commandManager.registerCommand(new PrintAscendingCommand(server, collectionManager));
        commandManager.registerCommand(new PrintDescendingCommand(server, collectionManager));
        commandManager.registerCommand(new RemoveByIdCommand(server, collectionManager));
        commandManager.registerCommand(new RemoveLowerCommand(server, collectionManager));
      //  commandManager.registerCommand(new SaveCommand(server, collectionManager));
        commandManager.registerCommand(new ShowCommand(server, collectionManager));
        commandManager.registerCommand(new UpdateCommand(server, collectionManager));
        commandManager.registerCommand(new HelpCommand(server, commandManager));
        commandManager.registerCommand(new RegisterCommand(server));
        commandManager.registerCommand(new AuthCommand(server));
    }
}