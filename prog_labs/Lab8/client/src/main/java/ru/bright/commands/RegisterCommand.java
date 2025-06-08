package ru.bright.commands;

import ru.bright.Client;
import ru.bright.UserRequest;
import ru.bright.commands.types.ClientCommand;

import java.io.IOException;

public class RegisterCommand extends ClientCommand {

    public RegisterCommand(Client client) {
        super(client,
                "register", "Регистрация",
                CommandType.REGISTER);
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
            getConsole().println("Creating new user");
            getConsole().println("Type login");
            String login = getConsole().readLine();
            getConsole().println("Type password");
            String password = getConsole().readLine();
            getClient().requestToServer(new UserRequest(getClient().getUser(),
                    "register " + login + " " + password,null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
