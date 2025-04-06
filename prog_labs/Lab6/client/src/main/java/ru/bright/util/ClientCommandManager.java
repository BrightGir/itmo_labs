package ru.bright.util;

import ru.bright.*;
import ru.bright.commands.CommandManager;
import ru.bright.commands.CommandType;
import ru.bright.commands.types.ClientCommand;

import java.io.IOException;
import java.util.Arrays;

public class ClientCommandManager extends CommandManager {

    private Client client;

    public ClientCommandManager(Client client) {
        this.client = client;
    }

    public boolean executeCommand(CommandType commandType, String line, String[] args) {

        if(!getCommandMap().containsKey(commandType)) {
            client.getConsole().printErr("Команды " + commandType.name() + " не существует");
            return false;
        }
        if(getCommandMap().get(commandType) == null) {
            try {
                client.requestToServer(new Request(line, null));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        if(((ClientCommand)getCommandMap().get(commandType)).execute(line,args)) {
            if (getCommandHistory().size() >= 13) {
                getCommandHistory().poll();
            }
            getCommandHistory().add(commandType.name().toLowerCase());
        }
        return true;
    }

    public boolean executeCommand(String line) {
        if(line == null || line.isEmpty()) return false;
        String cmdName = line.split(" ")[0];
        CommandType type;
        try{
            type = CommandType.valueOf(cmdName.toUpperCase());
        } catch (Exception e) {
            client.getConsole().printErr("Команды " + cmdName + " не существует");
            return false;
        }
        executeCommand(type, line, Arrays.copyOfRange(line.split(" "),
                1,line.split(" ").length));
        return true;
    }
}
