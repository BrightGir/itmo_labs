package ru.bright.commands;

import ru.bright.Client;
import ru.bright.User;
import ru.bright.UserRequest;
import ru.bright.commands.types.ClientCommand;

import java.io.IOException;

public class LoginCommand extends ClientCommand {

    public LoginCommand(Client client) {
        super(client,
                "auth", "Авторизация",
                CommandType.AUTH);
    }


    /**
     * Завершает программу
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String line, String[] arguments) {
        if(arguments.length != 0) {
            getConsole().println("Неверное использование команды.");
            return false;
        }
        try {
            getConsole().println("Type login");
            String login = getConsole().readLine();
            getConsole().println("Type password");
            String password = getConsole().readLine();
            getClient().requestToServer(new UserRequest(getClient().getUser(),
                    "auth " + login + " " + password,null));
            getClient().setUser(new User(login, password));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
