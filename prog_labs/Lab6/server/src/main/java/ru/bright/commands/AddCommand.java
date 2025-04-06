package ru.bright.commands;

import ru.bright.server.Server;
import ru.bright.commands.types.ServerObjectableCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;

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
       // getServer().getLogger().log(Level.SEVERE,"add object: " + object.toString());
        try {
            flat = (Flat) object;
            flat.setId(generateId());
        } catch (Exception e) {
            getServer().sendError(key,"Wrong flat object");
        }
        if(flat == null) return false;
        collectionManager.add(flat);
        getServer().sendOK(key,"Элемент успешно добавлен в коллекцию");
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
