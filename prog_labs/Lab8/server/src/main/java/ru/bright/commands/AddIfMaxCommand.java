package ru.bright.commands;

import ru.bright.ResponseStatus;
import ru.bright.UserRequest;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerObjectableCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;


/**
 * Класс добавления элемента в коллекцию
 */
public class AddIfMaxCommand extends ServerObjectableCommand {

    public AddIfMaxCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "add", "Добавляет элемент в коллекцию, если он больше максимального элемента в коллекции",
                CommandType.ADD_IF_MAX);

    }

    /**
     * Добавляет элемент в коллекцию, если сформированный элемент больше
     * максимального в коллекции
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
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
            flat.setId(generateId());
        } catch (Exception e) {
            getServer().sendError(user,key,"Wrong object");
        }
        if(flat == null) return false;
        Flat maxEl = collectionManager.getMaxElement();
        if(maxEl == null || flat.compareTo(collectionManager.getMaxElement()) > 0) {
            try {
                collectionManager.add(flat);
            } catch (SQLException e) {
                getServer().sendError(user,key,"Flat is not added to collection " + e.getMessage());
                return false;
            }
            getServer().sendOK(user,key,"Элемент успешно добавлен в коллекцию");
            getServer().sendAnother(new UserRequest(null,null,null),key, ResponseStatus.UPDATE,null);
        } else {
            getServer().sendOK(user,key,"Элемент не добавлен в коллекцию (меньше максимального)");
        }
        return true;
    }

    private long generateId() {
        CollectionManager manager = getServer().getCollectionManager();
        for(long i = 1; i <= 100000000;i ++) {
            if(manager.getById(i) == null) {
                return i;
            }
        }
        return 0;
    }
}
