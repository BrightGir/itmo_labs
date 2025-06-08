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
 * Класс команды добавления
 */
public class AddCommand extends ServerObjectableCommand {

    public AddCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "add", "Добавляет элемент в коллекцию", CommandType.ADD);

    }

    /**
     * Добавляет элемент в коллекцию
     * @param arguments аргументы
     * @return успешность операции
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        getServer().sendOK(user, key,"Wrong command using.");
        return false;
    }

    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] args, Object object) {
        if(args.length != 0) {
            getServer().sendOK(user, key,"Неверное использование команды.");
            return false;
        }
        Flat flat = null;
       // getServer().getLogger().log(Level.SEVERE,"add object: " + object.toString());
        try {
            flat = (Flat) object;
            flat.setId(generateId());
        } catch (Exception e) {
            getServer().sendError(user,key,"Wrong flat object");
        }
        if(flat == null) return false;
        try {
            collectionManager.add(flat);
        } catch (SQLException e) {
            getServer().sendError(user,key,"Flat is not added to collection " + e.getMessage());
            return false;
        }
        getServer().sendOK(user,key,"Элемент успешно добавлен в коллекцию");
        getServer().sendAnother(new UserRequest(null,null,null),key, ResponseStatus.UPDATE,null);
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
