package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.util.Console;


/**
 * Абстрактный класс для команд
 */
public abstract class Command {

    /**
     * Отображаемое имя команды
     */
    private String commandName;

    /**
     * Описание команды
     */
    private String description;

    /**
     * Консоль для взаимодействия с пользователем
     */
    protected Console console;

    /**
     * Тим команды
     */
    private CommandType commandType;

    public Command(Console console,
                   String commandName, String description,
                   CommandType commandType) {
        this.commandName = commandName;
        this.description = description;
        this.console = console;
        this.commandType = commandType;
    }

    /**
     * Выполнение команды
     * @param arguments аргументы
     * @return успешно ли выполнилась команда
     */
    public abstract boolean execute(String[] arguments);

    public String getDescription() {
        return description;
    }

    public String getCommandName() {
        return commandName;
    }

    public CommandType getCommandType() {
        return commandType;
    }

}
