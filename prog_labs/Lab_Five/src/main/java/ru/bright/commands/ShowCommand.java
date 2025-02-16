package ru.bright.commands;

import ru.bright.managers.CollectionManager;
import ru.bright.model.Flat;
import ru.bright.util.Console;

import java.util.Set;


/**
 * Команда для вывода информации о коллекции
 */
public class ShowCommand extends CollectionCommand{

    public ShowCommand(Console console, CollectionManager collectionManager) {
        super(console, collectionManager,
                "show",
                "Выводит коллекцию",
                CommandType.SHOW);

    }

    /**
     * Выводит информацию о текущем состоянии коллекции
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
        set.forEach(flat -> console.println(flat.toString()));
        return true;
    }
}
