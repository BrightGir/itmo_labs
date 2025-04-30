package ru.bright.commands;

import ru.bright.User;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCommand;
import ru.bright.managers.CollectionDatabaseManager;
import ru.bright.managers.ServerCommandManager;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;


/**
 * Команда выполнения скрипта
 */
public class ExecuteScriptCommand extends ServerCommand {


    private List<String> activeScripts;
    private ServerCommandManager commandManager;

    public ExecuteScriptCommand(Server server,
                                ServerCommandManager commandManager) {
        super(server,
                "execute_script",
                "Выполняет скрипт из файла",
                CommandType.EXECUTE_SCRIPT);
        this.commandManager = commandManager;
        this.activeScripts = new ArrayList<>();
    }

    /**
     * Выполняет команду из скрипта, заданного в аргументах
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length != 1) {
            getServer().sendOK(key,"Неверное использование команды.");
            return false;
        }
        String file = arguments[0];
        CollectionDatabaseManager fileManager = new CollectionDatabaseManager(getServer(), file);
        List<String> lines = fileManager.readLines();
        if(lines == null) {
            getServer().sendOK(key,"null");
            return false;
        }
        if(lines.isEmpty()) {
            getServer().sendOK(key,"Файл пуст");
            return false;
        }
        if(activeScripts.contains(file)) {
            getServer().sendOK(key,("Попытка создать бесконечную рекурсию"));
            return false;
        }
        activeScripts.add(file);
        lines.forEach(line -> commandManager.executeCommand(user, key,line));
        activeScripts.remove(file);
        return true;
    }
}
