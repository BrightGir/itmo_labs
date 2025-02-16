package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;
import ru.bright.util.requests.FlatRequest;

import java.util.Collection;

/**
 * Класс команды добавления
 */
public class AddCommand extends CollectionCommand {

    public AddCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "add", "Добавляет элемент в коллекцию", CommandType.ADD);

    }

    /**
     * Добавляет элемент в коллекцию
     * @param arguments аргументы
     * @return успешность операции
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        Flat flat = new FlatRequest(console).create();
        if(flat == null) return false;
        collectionManager.add(flat);
        console.println("Элемент успешно добавлен в коллекцию");
        return true;
    }
}
