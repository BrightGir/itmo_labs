package ru.bright;

import ru.bright.commands.AddCommand;
import ru.bright.managers.CollectionManager;
import ru.bright.managers.CommandManager;

import ru.bright.managers.FileManager;
import ru.bright.util.BasicConsole;
import ru.bright.util.Console;
import ru.bright.commands.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Главный класс приложения.
 * Запускает программу, инициализирует менеджеры и обрабатывает пользовательский ввод.
 */
public class Main {


    public static void main(String[] args) {
        String filePath =
                System.getProperty("user.dir") + File.separator + "collection.txt";
        BasicConsole console = new BasicConsole();
        CommandManager commandManager = new CommandManager(console);
        FileManager fileManager = new FileManager(console,
                filePath);
        CollectionManager collectionManager = new CollectionManager(fileManager);
        pullCommands(commandManager,console,collectionManager);
        if(collectionManager.loadJsonCollectionFromFile()) {
            console.println("Коллекция из файла была успешно загружена");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Программа запущена");
            console.setReader(reader);
            while (true) {
                System.out.print("> ");
                String line = reader.readLine();
                if(line == null) break;
                if(line.isEmpty()) {
                    continue;
                }
                commandManager.executeCommand(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка.");
        }
    }


    /**
     * Добавляет все доступные команды в менеджер команд.
     */
    private static void pullCommands(CommandManager commandManager, Console console, CollectionManager collectionManager) {
        commandManager.registerCommand(new AddCommand(console, collectionManager));
        commandManager.registerCommand(new AddIfMaxCommand(console, collectionManager));
        commandManager.registerCommand(new ClearCommand(console, collectionManager));
        commandManager.registerCommand(new ExecuteScriptCommand(console, commandManager));
        commandManager.registerCommand(new ExitCommand(console));
        commandManager.registerCommand(new FilterStartsWithNameCommand(console, collectionManager));
        commandManager.registerCommand(new HistoryCommand(console, commandManager));
        commandManager.registerCommand(new InfoCommand(console, collectionManager));
        commandManager.registerCommand(new PrintAscendingCommand(console, collectionManager));
        commandManager.registerCommand(new PrintDescendingCommand(console, collectionManager));
        commandManager.registerCommand(new RemoveByIdCommand(console, collectionManager));
        commandManager.registerCommand(new RemoveLowerCommand(console, collectionManager));
        commandManager.registerCommand(new SaveCommand(console, collectionManager));
        commandManager.registerCommand(new ShowCommand(console, collectionManager));
        commandManager.registerCommand(new UpdateCommand(console, collectionManager));
        commandManager.registerCommand(new HelpCommand(console, commandManager));
    }



}