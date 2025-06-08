package ru.bright.commands;

import ru.bright.ResponseStatus;
import ru.bright.UserRequest;
import ru.bright.commands.types.ServerCommand;
import ru.bright.server.Server;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;

public class AuthCommand extends ServerCommand {

    public AuthCommand(Server server) {
        super(server,
                "auth", "Авторизация",
                CommandType.AUTH);
    }

    /**
     * Сохраняет информацию о коллекции в файл
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(arguments.length < 2) {
            getServer().sendOK(user,key,"Wrong command using.");
            return true;
        }
        String login = arguments[0];
        String password = arguments[1];
        if(getServer().getAuthManager().verify(login,password)) {
            getServer().sendAnother(user,key, ResponseStatus.AUTH_PASSED, "Login successful.");
        } else {
            getServer().sendAnother(user,key,ResponseStatus.AUTH_FAILED, "Auth failed.");
        }
        return true;
    }
}
