package ru.bright.commands;

import ru.bright.User;
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
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(key,"Wrong command using.");
            return false;
        }
        Set<Flat> set = collectionManager.getUnmodifiableCollection();
        if(set == null || set.isEmpty()) {
            getServer().sendOK(key,"Collection is empty");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        set.forEach(flat -> sb.append(flat.toString() + "\n"));
        getServer().sendOK(key,sb.toString());
        return true;
    }

}
