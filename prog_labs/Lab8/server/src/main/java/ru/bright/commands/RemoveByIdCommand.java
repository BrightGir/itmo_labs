package ru.bright.commands;

import ru.bright.ResponseStatus;
import ru.bright.UserRequest;
import ru.bright.model.Flat;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.logging.Level;

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
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(!(arguments.length == 1)) {
            getServer().sendOK(user,key,"Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            getServer().sendError(user,key,"ID должно быть целым числом");
            return false;
        }
        Flat flat = collectionManager.getById(id);
        if(flat == null) {
            getServer().sendError(user,key,"Квартира с таким ID не найдена");
            return false;
        }
        try {
            if(!flat.getOwnerLogin().equals(user.getUser().getLogin())) {
                getServer().sendError(user,key,("Элемент c ID " + id + " принадлежит пользователю " + flat.getOwnerLogin()));
                return false;
            }
            collectionManager.deleteById(id);
        } catch (SQLException e) {
            getServer().sendError(user,key, "Error while deleting by id.");
            getServer().getLogger().log(Level.SEVERE,"SQL error", e);
            return false;
        }
        getServer().sendOK(user,key,"Элемент с ID " + id + " успешно удален");
        getServer().sendAnother(new UserRequest(null,null,null),key, ResponseStatus.UPDATE,null);
        return true;
    }
}
