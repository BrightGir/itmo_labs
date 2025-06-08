package ru.bright.commands;

import ru.bright.Client;
import ru.bright.Request;
import ru.bright.UserRequest;
import ru.bright.commands.types.ClientCommand;
import ru.bright.model.Flat;
import ru.bright.util.requests.FlatRequest;

import java.io.IOException;


/**
 * Класс добавления элемента в коллекцию
 */
public class AddIfMaxCommand extends ClientCommand {

    public AddIfMaxCommand(Client client) {
        super(client,
                "add", "Добавляет элемент в коллекцию, если он больше максимального элемента в коллекции",
                CommandType.ADD_IF_MAX);

    }

    /**
     * Добавляет элемент в коллекцию, если сформированный элемент больше
     * максимального в коллекции
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String line, String[] arguments) {
        if(arguments.length != 0) {
            getConsole().println("Неверное использование команды.");
            return false;
        }
        Flat flat = new FlatRequest(getConsole()).create();
        flat.setOwnerLogin(getClient().getUser().getLogin());
        if(flat == null) return false;
        try {
            getClient().requestToServer(new UserRequest(getClient().getUser(), line,flat));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
