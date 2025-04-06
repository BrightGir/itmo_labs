package ru.bright.commands;

import java.util.*;


/**
 * Класс для управления командами
 * Хранит команды и выполняет их
 */
public abstract class CommandManager {

    /**
     * Словарь доступных команд
     */
    private Map<CommandType, Command> commands;

    /**
     * История выполненных команд
     */
    private Queue<String> commandHistory;

    public CommandManager() {
        this.commands = new HashMap<CommandType, Command>();
        this.commandHistory = new ArrayDeque<>();
    }

    /**
     * Возвращает коллекцию доступных команд
     * @return доступные команды
     */
    public Collection<Command> getCommands() {
        return commands.values();
    }

    /**
     * Регистрирует команду для дальнейшей работы с ней
     * @param command команда
     */

    public void registerCommand(Command command) {
        commands.put(command.getCommandType(), command);
    }
    public void registerCommand(CommandType type) {
        commands.put(type, null);
    }

    /**
     * Выполняет команду
     * @param commandType тип команды
     * @param args аргументы
     * @return успешно ли выполнилась команда
     */
 // public boolean executeCommand(CommandType commandType, String[] args) {
 //     //console.println("args = " + args[0] + " " + args[1] + " " + args[2]);
 //     if(!commands.containsKey(commandType)) {
 //         console.printErr("Команды " + commandType.name() + " не существует");
 //         return false;
 //     }
 //     if(commands.get(commandType).execute(args)) {
 //         if (commandHistory.size() >= 13) {
 //             commandHistory.poll();
 //         }
 //         commandHistory.add(commandType.name().toLowerCase());
 //     }
 //     return true;
 // }
  //  public abstract boolean executeCommand(CommandType commandType, String[] args);

    /**
     * Извлекает команду из строки и выполняет ее
     * @param line строка
     * @return успешно ли выполнилась команда
     */
  //  public boolean executeCommand(String line) {
  //      if(line == null || line.isEmpty()) return false;
  //      String cmdName = line.split(" ")[0];
  //      CommandType type;
  //      try{
  //          type = CommandType.valueOf(cmdName.toUpperCase());
  //      } catch (Exception e) {
  //          console.printErr("Команды " + cmdName + " не существует");
  //          return false;
  //      }
  //      executeCommand(type, Arrays.copyOfRange(line.split(" "),
  //              1,line.split(" ").length));
  //      return true;
  //  }


    /**
     * Возвращает историю команд
     * @return история команд
     */
    public Queue<String> getCommandHistory() {
        return commandHistory;
    }

    public Map<CommandType, Command> getCommandMap() {
        return commands;
    }

}
