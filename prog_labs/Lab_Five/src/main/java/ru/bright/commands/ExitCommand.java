package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.util.Console;


/**
 * Команда завершения программы
 */
public class ExitCommand extends Command{

    public ExitCommand(Console console) {
        super(console,
                "exit", "Завершает работу программы",
                CommandType.EXIT);
    }


    /**
     * Завершает программу
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        console.println("Завершение работы программы");
        System.exit(0);
        return false;
    }
}
