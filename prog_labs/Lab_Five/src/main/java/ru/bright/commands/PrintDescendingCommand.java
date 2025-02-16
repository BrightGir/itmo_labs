package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;

import java.util.Comparator;
import java.util.Set;

/**
 * Команда для вывода элементов коллекции по убыванию
 */
public class PrintDescendingCommand extends CollectionCommand {

    public PrintDescendingCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "print_descending", "Выводит коллекцию в порядке убывания",
                CommandType.PRINT_ASCENDING);
    }

    /**
     * Выводит элементы коллекции в порядке убывания
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
        set.stream().sorted(Comparator.reverseOrder())
                .forEach(x -> console.println(x.toString()));
        return true;
    }
}
