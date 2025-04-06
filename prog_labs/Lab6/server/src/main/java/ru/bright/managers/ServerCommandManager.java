package ru.bright.managers;

import ru.bright.commands.CommandManager;
import ru.bright.commands.CommandType;
import ru.bright.commands.SaveCommand;
import ru.bright.server.Server;
import ru.bright.commands.types.ServerCommand;
import ru.bright.commands.types.ServerObjectableCommand;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class ServerCommandManager extends CommandManager {

    private Server server;
    private SaveCommand saveCommand;

    public ServerCommandManager(Server server) {
        this.server = server;

    }

    public void saveFile() {
        server.getCollectionManager().saveJsonCollectionToFile();
    }

 //   @Override
    public boolean executeCommand(SelectionKey key, CommandType commandType, String[] args) {
        //console.println("args = " + args[0] + " " + args[1] + " " + args[2]);
        if(!getCommandMap().containsKey(commandType)) {
            server.sendError(key, "Команды " + commandType.name() + " не существует");
            return false;
        }
        if(((ServerCommand)getCommandMap().get(commandType)).execute(key,args)) {
            if (getCommandHistory().size() >= 13) {
                getCommandHistory().poll();
            }
            getCommandHistory().add(commandType.name().toLowerCase());
        }
        return true;
    }

    public boolean executeCommand(SelectionKey key, String line) {
        if(line == null || line.isEmpty()) return false;
        String cmdName = line.split(" ")[0];
        CommandType type;
        try{
            type = CommandType.valueOf(cmdName.toUpperCase());
        } catch (Exception e) {
            server.sendOK(key,"Команды " + cmdName + " не существует");
            return false;
        }
        executeCommand(key, type, Arrays.copyOfRange(line.split(" "),
                1,line.split(" ").length));
        return true;
    }

    public boolean executeObjectableCommand(SelectionKey key, CommandType commandType, String[] args, Object object) {
        //console.println("args = " + args[0] + " " + args[1] + " " + args[2]);
        if(!getCommandMap().containsKey(commandType)) {
            server.sendError(key, "Команды " + commandType.name() + " не существует");
            return false;
        }
        if(((ServerObjectableCommand)getCommandMap().get(commandType)).execute(key,args, object)) {
            if (getCommandHistory().size() >= 13) {
                getCommandHistory().poll();
            }
            getCommandHistory().add(commandType.name().toLowerCase());
        }
        return true;
    }

    public boolean executeObjectableCommand(SelectionKey key, String line, Object object) {
        if(line == null || line.isEmpty()) return false;
        String cmdName = line.split(" ")[0];
        CommandType type;
        try{
            type = CommandType.valueOf(cmdName.toUpperCase());
        } catch (Exception e) {
            server.sendOK(key,"Команды " + cmdName + " не существует");
            return false;
        }
        executeObjectableCommand(key, type, Arrays.copyOfRange(line.split(" "),
                1,line.split(" ").length), object);
        return true;
    }



}
