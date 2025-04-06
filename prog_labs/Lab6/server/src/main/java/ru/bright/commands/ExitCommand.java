package ru.bright.commands;

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
    public boolean execute(SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        getServer().sendOK(key,"Завершение работы программы");
        System.exit(0);
        return true;
    }
}
