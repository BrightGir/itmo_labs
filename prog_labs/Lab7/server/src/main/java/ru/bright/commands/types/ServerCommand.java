package ru.bright.commands.types;

import ru.bright.Response;
import ru.bright.User;
import ru.bright.commands.Command;
import ru.bright.commands.CommandType;
import ru.bright.server.Server;

import java.nio.channels.SelectionKey;

public abstract class ServerCommand extends Command {

    private Server server;

    public ServerCommand(Server server, String description, String commandName, CommandType commandType) {
        super(commandName, description, commandType);
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public abstract boolean execute(User user, SelectionKey key, String[] args);
}
