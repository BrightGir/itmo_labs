package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;

import java.time.format.DateTimeFormatter;
import java.util.Set;


/**
 * Команда для сохранения коллекции
 */
public class SaveCommand extends CollectionCommand {

    public SaveCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "save", "Сохраняет коллекцию в файл",
                CommandType.SAVE);
    }

    /**
     * Сохраняет информацию о коллекции в файл
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        if(collectionManager.saveJsonCollectionToFile()) {
            console.println("Коллекция успешно сохранена");
            return true;
        }
        return false;
    }
}
