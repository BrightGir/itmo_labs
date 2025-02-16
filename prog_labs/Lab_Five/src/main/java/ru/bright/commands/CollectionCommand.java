package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.util.Console;


/**
 * Класс для команд, требующих для выполнения коллекцию
 */
public abstract class CollectionCommand extends Command{

    protected CollectionManager collectionManager;

    public CollectionCommand(Console console, CollectionManager collectionManager,
                             String commandName, String description,
                             CommandType commandType) {
        super(console, commandName,description,commandType);
        this.collectionManager = collectionManager;
    }

}
