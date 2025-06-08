package ru.bright.commands;
import ru.bright.ResponseStatus;
import ru.bright.UserRequest;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerObjectableCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.logging.Level;


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
    public boolean execute(UserRequest user,SelectionKey key, String[] arguments) {
        getServer().sendOK(user,key,"Wrong command using.");
        return false;
    }

    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] args, Object object) {
        if(args.length != 0) {
            getServer().sendOK(user,key,"Неверное использование команды.");
            return false;
        }
        Flat flat = null;
        try {
            flat = (Flat) object;
        } catch (Exception e) {
            getServer().sendError(user,key,"Wrong object");
        }
        if(flat == null) return false;
        try {
            collectionManager.removeLower(user.getUser(), flat);
        } catch (SQLException e) {
            getServer().sendError(user,key, "Error while removing lower.");
            getServer().getLogger().log(Level.SEVERE,"SQL error", e);
            return false;
        }
        getServer().sendOK(user,key,("Элементы, меньшие заданного, удалены из коллекции"));
        getServer().sendAnother(null,key, ResponseStatus.UPDATE,null);
        return true;
    }
}
