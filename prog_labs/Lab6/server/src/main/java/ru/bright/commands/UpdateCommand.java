package ru.bright.commands;

import ru.bright.server.Server;
import ru.bright.commands.types.ServerObjectableCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;

/**
 * Команда для обновления элементов
 */
public class UpdateCommand extends ServerObjectableCommand {

    public UpdateCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "update {id}", "Добавляет элемент в коллекцию",
                CommandType.UPDATE);

    }

    /**
     * Обновляет элемент с заданным ID из аргументов на сформированный
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
        if(!(args.length == 1)) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            getServer().sendError(key, "ID должно быть целым числом");
            return false;
        }
        Flat toUpdateFlat = collectionManager.getById(id);
        if(toUpdateFlat == null) {
            getServer().sendError(key,"Квартира с таким ID не найдена");
            return false;
        }
        Flat flat = null;
        try {
            flat = (Flat) object;
        } catch (Exception e) {
            getServer().sendError(key,"Wrong object");
        }
        if(flat == null) return false;

        toUpdateFlat.setArea(flat.getArea());
        toUpdateFlat.setCentralHeating(flat.getCentralHeating());
        toUpdateFlat.setFurnish(flat.getFurnish());
        toUpdateFlat.setName(flat.getName());
        toUpdateFlat.setNumberOfRooms(flat.getNumberOfRooms());
        toUpdateFlat.setCoordinates(flat.getCoordinates());
        toUpdateFlat.setHouse(flat.getHouse());
        toUpdateFlat.setTimeToMetroOnFoot(flat.getTimeToMetroOnFoot());

        getServer().sendOK(key,("Элемент c ID " + id + " обновлен"));
        return true;
    }
}
