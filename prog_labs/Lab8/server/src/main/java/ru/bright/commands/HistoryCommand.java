package ru.bright.commands;

import ru.bright.UserRequest;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCommand;

import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Команда для вывода истории команд
 */
public class HistoryCommand extends ServerCommand {

    private CommandManager commandManager;

    public HistoryCommand(Server server, CommandManager commandManager) {
        super(server,
                "history", "Выводит последние 13 команд",
                CommandType.HISTORY);
        this.commandManager = commandManager;
    }

    /**
     * Выводит последние 13 успешно выполненных команд (только имена)
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(UserRequest user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendOK(user,key,"Неверное использование команды.");
            return false;
        }
        Queue<String> cmds = commandManager.getCommandHistory();
        if(cmds.isEmpty()) {
            getServer().sendOK(user,key,"История команд пуста");
            return true;
        }
        //  getServer().sendOK(user,key,"История команд:");
        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger(1);
        sb.append("История команд: ");
        cmds.forEach(cmd -> {
            sb.append(i + ". " + cmd + "\n");
            i.addAndGet(1);
        });
        getServer().sendOK(user,key,sb.toString());
        return true;
    }

}
