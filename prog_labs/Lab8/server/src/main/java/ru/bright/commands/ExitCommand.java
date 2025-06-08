package ru.bright.commands;

import ru.bright.UserRequest;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCommand;

import java.nio.channels.SelectionKey;


/**
 * Команда завершения программы
 */
public class ExitCommand extends ServerCommand {

    public ExitCommand(Server server) {
        super(server,
                "exit", "Завершает работу программы",
                CommandType.EXIT);
    }


    /**
     * Завершает программу
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(user,key,"Неверное использование команды.");
            return false;
        }
        getServer().sendOK(user,key,"Завершение работы программы");
        System.exit(0);
        return true;
    }
}
