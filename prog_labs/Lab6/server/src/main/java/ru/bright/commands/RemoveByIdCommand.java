package ru.bright.commands;

import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;

import java.nio.channels.SelectionKey;

/**
 * Команда для удаления элемента по ID
 */
public class RemoveByIdCommand extends ServerCollectionCommand {

    public RemoveByIdCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "remove_by_id {id}",
                "Удаляет элемент с заданным ID", CommandType.REMOVE_BY_ID);

    }

    /**
     * Удаляет из коллекции элемент с заданным ID из аргументов
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(SelectionKey key, String[] arguments) {
        if(!(arguments.length == 1)) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            getServer().sendError(key,"ID должно быть целым числом");
            return false;
        }
        if(collectionManager.getById(id) == null) {
            getServer().sendError(key,"Квартира с таким ID не найдена");
            return false;
        }
        collectionManager.deleteById(id);
        getServer().sendOK(key,"Элемент с ID " + id + " успешно удален");
        return true;
    }
}
