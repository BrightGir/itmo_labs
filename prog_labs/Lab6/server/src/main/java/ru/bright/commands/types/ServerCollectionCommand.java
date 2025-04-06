package ru.bright.commands.types;

import ru.bright.commands.CommandType;
import ru.bright.server.Server;
import ru.bright.managers.CollectionManager;

/**
 * Класс для команд, требующих для выполнения коллекцию
 */
public abstract class ServerCollectionCommand extends ServerCommand {


    protected CollectionManager collectionManager;

    public ServerCollectionCommand(Server server, CollectionManager collectionManager, String description, String commandName, CommandType commandType) {
        super(server, description, commandName, commandType);
        this.collectionManager = collectionManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }


}
