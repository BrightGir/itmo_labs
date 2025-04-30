package ru.bright.commands;

import ru.bright.User;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCollectionCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;

import java.nio.channels.SelectionKey;
import java.time.format.DateTimeFormatter;
import java.util.Set;


/**
 * Выводит информацию о коллекции
 */
public class InfoCommand extends ServerCollectionCommand {

    public InfoCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "info", "Выводит информацию о коллекции",
                CommandType.INFO);
    }

    /**
     * Выводит информацию о текущем состоянии коллекции
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        Set<Flat> example = collectionManager.getUnmodifiableCollection();


        StringBuilder sb = new StringBuilder();
        sb.append("Тип коллекции: " + example.getClass().getSimpleName() + "\n");
        sb.append("Количество элементов: " + example.size() + "\n");
        sb.append("Дата создания: " + collectionManager
                        .getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        getServer().sendOK(key,sb.toString());
        return true;
    }
}
