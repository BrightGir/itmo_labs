package ru.bright.server;

import ru.bright.Response;
import ru.bright.ResponseStatus;
import ru.bright.User;
import ru.bright.UserRequest;
import ru.bright.commands.Command;
import ru.bright.commands.CommandType;
import ru.bright.commands.types.ServerObjectableCommand;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.sql.SQLException;

public class ServerProcessHandler {

    private Server server;

    public ServerProcessHandler(Server server) {
        this.server = server;
    }

    protected void handleRequest(SelectionKey key, UserRequest request)  {
        User user = request.getUser();
        if (!(request.getCommandLine().trim().equalsIgnoreCase("auth") ||
                request.getCommandLine().trim().equalsIgnoreCase("register"))) {
            if (user != null && !server.getAuthManager().verify(user.getLogin(), user.getPassword())) {
                server.responseToClient(key, new Response(ResponseStatus.AUTH_FAILED, null));
                return;
            }
        }

        if (request.getAttachedObject() == null) {
            server.getServerCommandManager().executeCommand(user, key, request.getCommandLine());
        } else {
            String cmdName = request.getCommandLine().split(" ")[0];
            CommandType type;
            try {
                type = CommandType.valueOf(cmdName.toUpperCase());
            } catch (Exception e) {
                server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong command"));
                return;
            }
            if (!server.getServerCommandManager().getCommandMap().containsKey(type)) {
                server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong command"));
                return;
            }
            Command cmd = server.getServerCommandManager().getCommandMap().get(type);
            if (cmd instanceof ServerObjectableCommand) {
                server.getServerCommandManager().executeObjectableCommand(user, key, request.getCommandLine(), request.getAttachedObject());
            }
        }
    }
}
