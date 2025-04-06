package ru.bright;

import ru.bright.commands.*;
import ru.bright.util.BasicConsole;
import ru.bright.util.ClientCommandManager;

import java.io.IOException;

public class Main {


    public static void main(String[] args) {
        BasicConsole console = new BasicConsole();
        Client client = new Client(console, 8080);
        ClientCommandManager commandManager = new ClientCommandManager(client);
        pullCommands(commandManager,client);
        System.out.println("Программа запущена");
        client.setCommandManager(commandManager);
        try {
            client.openConnection();
            client.startListening();
        } catch (IOException e) {
            e.printStackTrace();
            console.printErr("Error while opening connection: " + e.getMessage());
        }

    }

    private static void pullCommands(CommandManager commandManager, Client client) {
        commandManager.registerCommand(new AddCommand(client));
        commandManager.registerCommand(new AddIfMaxCommand(client));
        commandManager.registerCommand(CommandType.CLEAR);
        commandManager.registerCommand(CommandType.EXECUTE_SCRIPT);
        commandManager.registerCommand(new ExitCommand(client));
        commandManager.registerCommand(CommandType.FILTER_STARTS_WITH_NAME);
        commandManager.registerCommand(CommandType.HISTORY);
        commandManager.registerCommand(CommandType.INFO);
        commandManager.registerCommand(CommandType.PRINT_ASCENDING);
        commandManager.registerCommand(CommandType.PRINT_DESCENDING);
        commandManager.registerCommand(CommandType.REMOVE_BY_ID);
        commandManager.registerCommand(new RemoveLowerCommand(client));
      //  commandManager.registerCommand(CommandType.SAVE);
        commandManager.registerCommand(CommandType.SHOW);
        commandManager.registerCommand(new UpdateCommand(client));
        commandManager.registerCommand(CommandType.HELP);
    }
}