package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;
import ru.bright.util.requests.FlatRequest;
import java.util.Set;

/**
 * Команда для удаления элемента по ID
 */
public class RemoveByIdCommand extends CollectionCommand {

    public RemoveByIdCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "remove_by_id {id}",
                "Удаляет элемент с заданным ID", CommandType.REMOVE_BY_ID);

    }

    /**
     * Удаляет из коллекции элемент с заданным ID из аргументов
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(!(arguments.length == 1)) {
            console.println("Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            console.printErr("ID должно быть целым числом");
            return false;
        }
        if(collectionManager.getById(id) == null) {
            console.printErr("Квартира с таким ID не найдена");
            return false;
        }
        collectionManager.deleteById(id);
        console.println("Элемент с ID " + id + " успешно удален");
        return true;
    }
}
