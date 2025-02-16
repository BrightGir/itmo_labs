package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;
import ru.bright.util.requests.FlatRequest;


/**
 * Класс добавления элемента в коллекцию
 */
public class AddIfMaxCommand extends CollectionCommand {

    public AddIfMaxCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
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
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        Flat flat = new FlatRequest(console).create();
        if(flat == null) return false;
        Flat maxEl = collectionManager.getMaxElement();
        if(maxEl == null || flat.compareTo(collectionManager.getMaxElement()) > 0) {
            collectionManager.add(flat);
            console.println("Элемент успешно добавлен в коллекцию");
        } else {
            console.println("Элемент не добавлен в коллекцию (меньше максимального)");
        }
        return true;
    }
}
