package ru.bright.commands;

import ru.bright.managers.CommandManager;
import ru.bright.util.Console;

/**
 * Команда для вывода справки
 */
public class HelpCommand extends Command {

    private CommandManager commandManager;
    public HelpCommand(Console console, CommandManager commandManager) {
        super(console, "help", "Выводит справку по командам", CommandType.HELP);
        this.commandManager = commandManager;
    }

    /**
     * Выводит справку по доступных комаднам
     * @param arguments аргументы
     * @return успешность выполения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        console.println("Справка:");
        commandManager.getCommands().forEach(command -> {
           console.println(command.getCommandName() + " - " + command.getDescription());
        });
        return true;
    }
}
