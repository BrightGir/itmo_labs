package ru.bright.commands;

import ru.bright.UserRequest;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;
import java.util.Set;

/**
 * Команда для вывода отсортированных по возрастанию элементов коллекции
 */
public class PrintAscendingCommand extends ServerCollectionCommand {

    public PrintAscendingCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "print_ascending", "Выводит коллекцию в порядке возрастания",
                CommandType.PRINT_ASCENDING);
    }

    /**
     * Выводит элементы коллекции в порядке возрастания
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(user,key,"Неверное использование команды.");
            return false;
        }
        Set<Flat> set = collectionManager.getUnmodifiableCollection();
        if(set == null || set.isEmpty()) {
            getServer().sendOK(user,key,"Коллекция пуста");
            return true;
        }
        StringBuilder sb = new StringBuilder();
        set.stream().sorted().forEach(x -> sb.append(x.toString() + "\n"));
        getServer().sendOK(user,key,sb.toString());
        return true;
    }
}
