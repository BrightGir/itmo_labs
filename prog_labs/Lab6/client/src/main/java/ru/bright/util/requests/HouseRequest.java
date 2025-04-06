package ru.bright.util.requests;

import ru.bright.model.House;
import ru.bright.util.Console;

import java.io.IOException;

/**
 * Класс для запроса объекта {@link House}
 */
public class HouseRequest extends Request {

    public HouseRequest(Console console) {
        super(console);
    }

    /*
      private String name; //Поле не может быть null
    private Integer year; //Максимальное значение поля: 117, Значение поля должно быть больше 0
    private long numberOfFlatsOnFloor; //Значение поля должно быть больше 0
     */

    /**
     * Формирует объект {@link House} на основе пользовательского ввода
     * @return
     */
    @Override
    public House create() {
        try {
            console.println("Введите название дома: ");
            String name = getValidString();
            console.println("Введите, сколько лет дому: ");
            int year = getValidInt(x -> (x <= 117 && x > 0), "Значение должно быть от 1 до 117. Повторите ввод:");
            console.println("Введите количество квартир на этаже: ");
            long numberOfFlatsOnFloor = getValidLong(x -> x > 0, "Значение должно быть больше 0. Повторите ввод: ");
            return new House(name, year, numberOfFlatsOnFloor);
        } catch (IOException e){
            console.printErr("Ошибка чтения");
            return null;
        }
    }
}
