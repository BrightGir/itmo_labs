package ru.bright.commands;

import ru.bright.User;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCommand;

import java.nio.channels.SelectionKey;
import java.util.logging.Level;

/**
 * Команда для вывода справки
 */
public class HelpCommand extends ServerCommand {

    private CommandManager commandManager;
    public HelpCommand(Server server, CommandManager commandManager) {
        super(server, "help", "Выводит справку по командам", CommandType.HELP);
        this.commandManager = commandManager;
    }

    /**
     * Выводит справку по доступных комаднам
     * @param arguments аргументы
     * @return успешность выполения команды
     */
    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Справка: " +  "\n");
      //  sb.append("NEED: " +  "\n");
        commandManager.getCommands().forEach(command -> {
           sb.append(command.getCommandName() + " - " + command.getDescription() + "\n");
        });
      //  getServer().getLogger().log(Level.SEVERE, sb.toString());
        getServer().sendOK(key,sb.toString());
        return true;
    }
}
