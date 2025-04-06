package ru.bright.commands;

import ru.bright.Client;
import ru.bright.commands.types.ClientCommand;


/**
 * Команда завершения программы
 */
public class ExitCommand extends ClientCommand {

    public ExitCommand(Client client) {
        super(client,
                "exit", "Завершает работу программы",
                CommandType.EXIT);
    }


    /**
     * Завершает программу
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String line, String[] arguments) {
        if(arguments.length != 0) {
            getConsole().println("Неверное использование команды.");
            return false;
        }
        getConsole().println("Завершение работы программы");
        System.exit(0);
        return false;
    }
}
