package ru.bright.commands;

import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;

import java.nio.channels.SelectionKey;


/**
 * Команда для сохранения коллекции
 */
public class SaveCommand extends ServerCollectionCommand {

    public SaveCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "save", "Сохраняет коллекцию в файл",
                CommandType.SAVE);
    }

    /**
     * Сохраняет информацию о коллекции в файл
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        if(collectionManager.saveJsonCollectionToFile()) {
            getServer().sendOK(key,"Коллекция успешно сохранена");
            return true;
        }
        return true;
    }
}
