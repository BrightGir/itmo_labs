package ru.bright.commands;

import ru.bright.Client;
import ru.bright.Request;
import ru.bright.UserRequest;
import ru.bright.commands.types.ClientCommand;
import ru.bright.model.Flat;
import ru.bright.util.requests.FlatRequest;

import java.io.IOException;


/**
 * Команда для удаления элементов коллекции меньше заданного
 */
public class RemoveLowerCommand extends ClientCommand {


    public RemoveLowerCommand(Client client) {
        super(client,
                "remove_lower",
                "Удаляет элементы в коллекцию, меньшие заданного",
                CommandType.REMOVE_LOWER);

    }

    /**
     * Удаляет все элементы коллекции, которые меньше заданного
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
