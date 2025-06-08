package ru.bright.commands;

import ru.bright.ResponseStatus;
import ru.bright.UserRequest;
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
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(arguments.length < 2) {
            getServer().sendOK(user,key,"Wrong command using.");
            return true;
        }
        String login = arguments[0];
        String password = arguments[1];
        try {
            if (getServer().getAuthManager().hasLogin(login)) {
                getServer().sendAnother(user,key, ResponseStatus.LOGIN_BUSY, "Login is already busy");
                return true;
            }
            getServer().getAuthManager().register(login,password);
            getServer().sendAnother(user,key, ResponseStatus.REGISTER_SUCCESSFUL, "Register successful.");
        } catch (SQLException e) {
            getServer().sendError(user,key,"Internal error threw");
        }
        return true;
    }
}
