package ru.bright.commands;

import ru.bright.UserRequest;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;

import java.nio.channels.SelectionKey;


/**
 * Команда очищения коллекции
 */
public class ClearCommand extends ServerCollectionCommand {

    public ClearCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "clear", "Очищает коллекцию",
                CommandType.CLEAR);
    }

    /**
     * Очищает коллекцию
     * @param arguments аргументы
     * @return успешность операции
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(user,key,"Неверное использование команды.");
            return false;
        }
        collectionManager.clear(user.getUser());
        getServer().sendOK(user,key,"Коллекция была очищена");
        return true;
    }
}
