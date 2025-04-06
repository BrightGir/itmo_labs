package ru.bright.commands;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerObjectableCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;


/**
 * Команда для удаления элементов коллекции меньше заданного
 */
public class RemoveLowerCommand extends ServerObjectableCommand {


    public RemoveLowerCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "remove_lower",
                "Удаляет элементы в коллекцию, меньшие заданного",
                CommandType.REMOVE_LOWER);

    }

    /**
     * Удаляет все элементы коллекции, которые меньше заданного
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(SelectionKey key, String[] arguments) {
        getServer().sendOK(key,"Wrong command using.");
        return false;
    }

    @Override
    public boolean execute(SelectionKey key, String[] args, Object object) {
        if(args.length != 0) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        Flat flat = null;
        try {
            flat = (Flat) object;
        } catch (Exception e) {
            getServer().sendError(key,"Wrong object");
        }
        if(flat == null) return false;
        collectionManager.removeLower(flat);
        getServer().sendOK(key,("Элементы, меньшие заданного, удалены из коллекции"));
        return true;
    }
}
