package ru.bright.commands;

import ru.bright.server.Server;
import ru.bright.commands.types.ServerObjectableCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;


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
            flat.setId(generateId());
        } catch (Exception e) {
            getServer().sendError(key,"Wrong object");
        }
        if(flat == null) return false;
        Flat maxEl = collectionManager.getMaxElement();
        if(maxEl == null || flat.compareTo(collectionManager.getMaxElement()) > 0) {
            collectionManager.add(flat);
            getServer().sendOK(key,"Элемент успешно добавлен в коллекцию");
        } else {
            getServer().sendOK(key,"Элемент не добавлен в коллекцию (меньше максимального)");
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
