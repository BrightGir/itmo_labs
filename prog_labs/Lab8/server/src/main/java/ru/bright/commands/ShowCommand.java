package ru.bright.commands;

import ru.bright.UserRequest;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;
import java.util.Set;


/**
 * Команда для вывода информации о коллекции
 */
public class ShowCommand extends ServerCollectionCommand {

    public ShowCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "show",
                "Выводит коллекцию",
                CommandType.SHOW);
    }

    /**
     * Выводит информацию о текущем состоянии коллекции
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(user,key,"Wrong command using.");
            return false;
        }
        Set<Flat> set = collectionManager.getUnmodifiableCollection();
        getServer().sendObject(user,key,set);
        return true;
    }

}
