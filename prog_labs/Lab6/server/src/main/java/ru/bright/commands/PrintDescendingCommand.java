package ru.bright.commands;

import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;
import java.util.Comparator;
import java.util.Set;

/**
 * Команда для вывода элементов коллекции по убыванию
 */
public class PrintDescendingCommand extends ServerCollectionCommand {

    public PrintDescendingCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "print_descending", "Выводит коллекцию в порядке убывания",
                CommandType.PRINT_DESCENDING);
    }

    /**
     * Выводит элементы коллекции в порядке убывания
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        Set<Flat> set = collectionManager.getUnmodifiableCollection();
        if(set == null || set.isEmpty()) {
            getServer().sendOK(key,"Коллекция пуста");
            return true;
        }
        StringBuilder sb = new StringBuilder();
        set.stream().sorted(Comparator.reverseOrder())
                .forEach(x -> sb.append(x.toString() + "\n"));
        getServer().sendOK(key,sb.toString());
        return true;
    }
}
