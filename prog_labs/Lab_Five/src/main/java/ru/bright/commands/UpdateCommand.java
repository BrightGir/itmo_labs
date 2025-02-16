package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;
import ru.bright.util.requests.FlatRequest;

/**
 * Команда для обновления элементов
 */
public class UpdateCommand extends CollectionCommand {

    public UpdateCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "update {id}", "Добавляет элемент в коллекцию",
                CommandType.UPDATE);

    }

    /**
     * Обновляет элемент с заданным ID из аргументов на сформированный
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(!(arguments.length == 1)) {
            console.println("Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            console.printErr("ID должно быть целым числом");
            return false;
        }
        Flat toUpdateFlat = collectionManager.getById(id);
        if(toUpdateFlat == null) {
            console.printErr("Квартира с таким ID не найдена");
            return false;
        }
        Flat flat = new FlatRequest(console).create();
        if(flat == null) return false;

        toUpdateFlat.setArea(flat.getArea());
        toUpdateFlat.setCentralHeating(flat.getCentralHeating());
        toUpdateFlat.setFurnish(flat.getFurnish());
        toUpdateFlat.setName(flat.getName());
        toUpdateFlat.setNumberOfRooms(flat.getNumberOfRooms());
        toUpdateFlat.setCoordinates(flat.getCoordinates());
        toUpdateFlat.setHouse(flat.getHouse());
        toUpdateFlat.setTimeToMetroOnFoot(flat.getTimeToMetroOnFoot());

        console.println("Элемент c ID " + id + " обновлен");
        return true;
    }
}
