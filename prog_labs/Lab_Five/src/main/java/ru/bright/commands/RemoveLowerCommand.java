package ru.bright.commands;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;
import ru.bright.util.requests.FlatRequest;


/**
 * Команда для удаления элементов коллекции меньше заданного
 */
public class RemoveLowerCommand extends CollectionCommand {


    public RemoveLowerCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
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
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        Flat flat = new FlatRequest(console).create();
        if(flat == null) return false;
        collectionManager.removeLower(flat);
        console.println("Элементы, меньшие заданного, удалены из коллекции");
        return true;
    }
}
