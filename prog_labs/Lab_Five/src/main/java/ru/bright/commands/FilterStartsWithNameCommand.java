package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;
import ru.bright.util.requests.FlatRequest;


/**
 * Команда вывода отфильтрованных элементов
 */
public class FilterStartsWithNameCommand extends CollectionCommand{

    public FilterStartsWithNameCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "filter_starts_with_name {name}",
                "Выводит элементы коллекции, имена которых начинаются с заданной строки",
                CommandType.FILTER_STARTS_WITH_NAME);

    }

    /**
     * Выводит элементы, имена которых начинаются с заданной подстроки из аргументов
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 1) {
            console.println("Неверное использование команды.");
            return false;
        }
        String name = arguments[0];
        if(name.isEmpty()) {
            console.printErr("Имя не может быть пустым");
            return false;
        }
        collectionManager.filterStartsWithName(name).forEach(x ->
                console.println(x.toString()));
        return true;
    }
}
