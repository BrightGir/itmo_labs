package ru.bright.commands;


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
     * Тим команды
     */
    private CommandType commandType;

    public Command(String commandName, String description,
                   CommandType commandType) {
        this.commandName = commandName;
        this.description = description;
        this.commandType = commandType;
    }


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
