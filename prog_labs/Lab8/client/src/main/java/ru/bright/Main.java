package ru.bright;

import javafx.application.Application;
import javafx.scene.text.Font;
import ru.bright.commands.*;
import ru.bright.gui.AuthWindow;
import ru.bright.gui.MainApp;
import ru.bright.gui.MainWindow;
import ru.bright.util.BasicConsole;
import ru.bright.util.ClientCommandManager;
import ru.bright.utils.Config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final ExecutorService exec = Executors.newFixedThreadPool(1, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public static void main(String[] args) {
        BasicConsole console = new BasicConsole();
        Client client = new Client(console, 8080);
        ClientCommandManager commandManager = new ClientCommandManager(client);
        pullCommands(commandManager,client);
        System.out.println("Программа запущена");
        client.setCommandManager(commandManager);
        Font.loadFont(MainWindow.class.getResourceAsStream("/fonts/Ubuntu-Bold.ttf"), 20);
        Font.loadFont(MainWindow.class.getResourceAsStream("/fonts/Ubuntu-Regular.ttf"), 20);
        Font.loadFont(MainWindow.class.getResourceAsStream("/fonts/Ubuntu-Italic.ttf"), 20);

        Config config = new Config();
        MainApp.setClient(client);
        MainApp.setConfig(config);
        exec.submit(() -> {
            try {
                client.openConnection();
                client.startListening();
            } catch (IOException e) {
                e.printStackTrace();
                console.printErr("Error while opening connection: " + e.getMessage());
            }
        });

        Application.launch(MainApp.class);
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
        commandManager.registerCommand(new RegisterCommand(client));
        commandManager.registerCommand(new LoginCommand(client));
        commandManager.registerCommand(CommandType.REMOVE_BY_ID);
        commandManager.registerCommand(new RemoveLowerCommand(client));
      //  commandManager.registerCommand(CommandType.SAVE);
        commandManager.registerCommand(CommandType.SHOW);
        commandManager.registerCommand(new UpdateCommand(client));
        commandManager.registerCommand(CommandType.HELP);
    }
}