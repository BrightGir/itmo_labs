package ru.bright.commands;

import ru.bright.User;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;

import java.nio.channels.SelectionKey;


/**
 * Команда вывода отфильтрованных элементов
 */
public class FilterStartsWithNameCommand extends ServerCollectionCommand {

    public FilterStartsWithNameCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "filter_starts_with_name {name}",
                "Выводит элементы коллекции, имена которых начинаются с заданной строки",
                CommandType.FILTER_STARTS_WITH_NAME);

    }

    /**
     * Выводит элементы, имена которых начинаются с заданной подстроки из аргументов
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length != 1) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        String name = arguments[0];
        if(name.isEmpty()) {
            getServer().sendOK(key,"Имя не может быть пустым");
            return false;
        }
        StringBuilder sb = new StringBuilder();;
        collectionManager.filterStartsWithName(name).forEach(x ->
                sb.append(x.toString() + "\n"));
        getServer().sendOK(key,sb.toString());
        return true;
    }
}
