package ru.bright;

import ru.bright.commands.*;
import ru.bright.managers.CollectionManager;
import ru.bright.managers.FileManager;
import ru.bright.managers.ServerCommandManager;
import ru.bright.server.Server;


import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class.getName());

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
        FileManager fileManager = new FileManager(server,
                filePath);
        CollectionManager collectionManager = new CollectionManager(fileManager);
        pullCommands(server,commandManager,collectionManager);
        server.setServerCommandManager(commandManager);
        server.setLogger(LOGGER);
        server.setCollectionManager(collectionManager);

        if(collectionManager.loadJsonCollectionFromFile()) {
            LOGGER.log(Level.INFO,"Collection from the file was successfully loaded");
        }

        server.startServer();

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
    }
}