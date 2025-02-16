package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;

import java.util.Set;

/**
 * Команда для вывода отсортированных по возрастанию элементов коллекции
 */
public class PrintAscendingCommand extends CollectionCommand {

    public PrintAscendingCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "print_ascending", "Выводит коллекцию в порядке возрастания",
                CommandType.PRINT_ASCENDING);
    }

    /**
     * Выводит элементы коллекции в порядке возрастания
     * @param arguments аргументы
     * @return успешность выполнения команды
     */
    @Override
    public boolean execute(String[] arguments) {
        if(arguments.length != 0) {
            console.println("Неверное использование команды.");
            return false;
        }
        Set<Flat> set = collectionManager.getUnmodifiableCollection();
        if(set == null || set.isEmpty()) {
            console.println("Коллекция пуста");
            return true;
        }
        set.stream().sorted().forEach(x -> console.println(x.toString()));
        return true;
    }
}
