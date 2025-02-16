package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;

import java.time.format.DateTimeFormatter;
import java.util.Set;


/**
 * Выводит информацию о коллекции
 */
public class InfoCommand extends CollectionCommand {

    public InfoCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "info", "Выводит информацию о коллекции",
                CommandType.INFO);
    }

    /**
     * Выводит информацию о текущем состоянии коллекции
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        Set<Flat> example = collectionManager.getUnmodifiableCollection();
        console.println("Тип коллекции: " + example.getClass().getSimpleName());
        console.println("Количество элементов: " + example.size());
        console.println("Дата создания: " + collectionManager
                        .getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return true;
    }
}
