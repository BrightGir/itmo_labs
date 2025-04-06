package ru.bright.commands.types;

import ru.bright.commands.CommandType;
import ru.bright.server.Server;
import ru.bright.managers.CollectionManager;

import java.nio.channels.SelectionKey;

public abstract class ServerObjectableCommand extends ServerCollectionCommand {


    public ServerObjectableCommand(Server server, CollectionManager collectionManager, String description, String commandName, CommandType commandType) {
        super(server, collectionManager, description, commandName, commandType);
    }

    public abstract boolean execute(SelectionKey key, String[] args, Object object);


}
