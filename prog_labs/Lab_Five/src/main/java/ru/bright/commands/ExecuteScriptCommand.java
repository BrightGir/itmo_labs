package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.managers.CommandManager;
import ru.bright.managers.FileManager;
import ru.bright.util.Console;

import java.util.ArrayList;
import java.util.List;


/**
 * Команда выполнения скрипта
 */
public class ExecuteScriptCommand extends Command {


    private List<String> activeScripts;
    private CommandManager commandManager;

    public ExecuteScriptCommand(Console console,
                                       CommandManager commandManager) {
        super(console,
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
    public boolean execute(String[] arguments) {
        if(arguments.length != 1) {
            console.println("Неверное использование команды.");
            return false;
        }
        String file = arguments[0];
        FileManager fileManager = new FileManager(console, file);
        List<String> lines = fileManager.readLines();
        if(lines == null) {

            return false;
        }
        if(lines.isEmpty()) {
            console.println("Файл пуст");
            return true;
        }
        if(activeScripts.contains(file)) {
            console.printErr("Попытка создать бесконечную рекурсию");
            return true;
        }
        activeScripts.add(file);
        lines.forEach(line -> commandManager.executeCommand(line));
        activeScripts.remove(file);
        return true;
    }
}
