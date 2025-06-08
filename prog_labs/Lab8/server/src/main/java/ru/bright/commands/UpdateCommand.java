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
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        getServer().sendOK(user,key,"Wrong command using.");
        return false;
    }


    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] args, Object object) {
        if(!(args.length == 1)) {
            getServer().sendOK(user,key,"Неверное использование команды.");
            return false;
        }
        //    getServer().getLogger().log(Level.INFO,"Execute UPDATE command, object=" + object.toString());
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            getServer().sendError(user,key, "ID должно быть целым числом");
            return false;
        }
        Flat toUpdateFlat = collectionManager.getById(id);
        if(toUpdateFlat == null) {
            getServer().sendError(user,key,"Квартира с таким ID не найдена");
            return false;
        }
        Flat flat = null;
        try {
            flat = (Flat) object;
        } catch (Exception e) {
            getServer().sendError(user,key,"Wrong object");
        }
        if(flat == null) return false;

        if(!toUpdateFlat.getOwnerLogin().equals(user.getUser().getLogin())) {
            getServer().sendError(user,key,("Элемент c ID " + id + " принадлежит пользователю " + toUpdateFlat.getOwnerLogin()));
            return false;
        }


        toUpdateFlat.setArea(flat.getArea());
        toUpdateFlat.setCentralHeating(flat.getCentralHeating());
        toUpdateFlat.setFurnish(flat.getFurnish());
        toUpdateFlat.setName(flat.getName());
        toUpdateFlat.setNumberOfRooms(flat.getNumberOfRooms());
        toUpdateFlat.setCoordinates(flat.getCoordinates());
        toUpdateFlat.setHouse(flat.getHouse());
        toUpdateFlat.setTimeToMetroOnFoot(flat.getTimeToMetroOnFoot());


        try {
            collectionManager.update(toUpdateFlat);
            getServer().sendOK(user,key,("Элемент c ID " + id + " обновлен"));
            getServer().sendAnother(new UserRequest(null,null,null),key, ResponseStatus.UPDATE,null);
        } catch (SQLException e) {
            getServer().sendError(user,key, "Error while updating flat.");
            getServer().getLogger().log(Level.SEVERE,"SQL error", e);
            return false;
        }


        return true;
    }
}
