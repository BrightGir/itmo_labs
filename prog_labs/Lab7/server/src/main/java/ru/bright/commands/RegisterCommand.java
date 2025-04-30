package ru.bright.commands;

import ru.bright.User;
import ru.bright.commands.types.ServerCommand;
import ru.bright.server.Server;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;

public class RegisterCommand extends ServerCommand {

    public RegisterCommand(Server server) {
        super(server,
                "register", "Регистрация",
                CommandType.REGISTER);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length < 2) {
            getServer().sendOK(key,"Wrong command using.");
            return true;
        }
        String login = arguments[0];
        String password = arguments[1];
        try {
            if (getServer().getAuthManager().hasLogin(login)) {
                getServer().sendError(key,"Login is already busy");
                return true;
            }
            getServer().getAuthManager().register(login,password);
            getServer().sendOK(key,"Register successful.");
        } catch (SQLException e) {
            getServer().sendError(key,"Internal error threw");
        }
        return true;
    }
}
