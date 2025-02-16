package ru.bright.util.requests;

import ru.bright.model.Coordinates;
import ru.bright.model.Furnish;
import ru.bright.model.House;
import ru.bright.util.Console;

import java.io.IOException;

/**
 * Класс для запроса объекта {@link Coordinates}
 */
public class CoordinatesRequest extends Request {

    public CoordinatesRequest(Console console) {
        super(console);
    }
/*
    private float x; //Значение поля должно быть больше -699
    private Integer y; //Поле не может быть null
 */

    /**
     * Формирует объект {@link Coordinates} на основе пользовательского ввода
     * @return
     */
    @Override
    public Coordinates create() {
        try {
            console.println("Введите координату x: ");
            float x = getValidFloat(b -> b > -699, "Значение должно быть больше -699. Повторите ввод: ");
            console.println("Введите координату y: ");
            int y = getValidInt();
            return new Coordinates(x, y);
        } catch (IOException e){
            console.printErr("Ошибка чтения");
            return null;
        }
    }
}
