package ru.bright.commands;

import ru.bright.Client;
import ru.bright.Request;
import ru.bright.commands.types.ClientCommand;
import ru.bright.model.Flat;
import ru.bright.util.requests.FlatRequest;

import java.io.IOException;

/**
 * Команда для обновления элементов
 */
public class UpdateCommand extends ClientCommand {

    public UpdateCommand(Client client) {
        super(client,
                "update {id}", "Добавляет элемент в коллекцию",
                CommandType.UPDATE);

    }

    /**
     * Обновляет элемент с заданным ID из аргументов на сформированный
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String line, String[] arguments) {
        if(!(arguments.length == 1)) {
            getConsole().println("Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            getConsole().printErr("ID должно быть целым числом");
            return false;
        }
        Flat flat = new FlatRequest(getConsole()).create();
        if(flat == null) return false;
        try {
            getClient().requestToServer(new Request(line,flat));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
