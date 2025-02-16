package ru.bright.commands;

import ru.bright.managers.CommandManager;
import ru.bright.util.Console;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Команда для вывода истории команд
 */
public class HistoryCommand extends Command {

    private CommandManager commandManager;

    public HistoryCommand(Console console, CommandManager commandManager) {
        super(console,
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
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        Queue<String> cmds = commandManager.getCommandHistory();
        if(cmds.isEmpty()) {
            console.println("История команд пуста");
            return true;
        }
        console.println("История команд:");
        AtomicInteger i = new AtomicInteger(1);
        cmds.forEach(cmd -> {
            console.println(i + ". " + cmd);
            i.addAndGet(1);
        });
        return true;
    }

}
