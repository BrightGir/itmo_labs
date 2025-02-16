package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;

import java.time.format.DateTimeFormatter;
import java.util.Set;


/**
 * Команда очищения коллекции
 */
public class ClearCommand extends CollectionCommand {

    public ClearCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "clear", "Очищает коллекцию",
                CommandType.CLEAR);
    }

    /**
     * Очищает коллекцию
     * @param arguments аргументы
     * @return успешность операции
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        collectionManager.clear();
        console.println("Коллекция была очищена");
        return true;
    }
}
