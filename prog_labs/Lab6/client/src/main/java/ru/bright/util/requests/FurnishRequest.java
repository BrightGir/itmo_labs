package ru.bright.util.requests;
import ru.bright.model.Furnish;
import ru.bright.util.Console;

import java.io.IOException;


/**
 * Класс для запроса объекта {@link Furnish}
 */
public class FurnishRequest extends Request {

    public FurnishRequest(Console console) {
        super(console);
    }

    /**
     * Формирует объект {@link Furnish} на основе пользовательского ввода
     * @return
     */
    @Override
    public Furnish create() {
        Furnish f;
        try {
            console.println("Введите значение для обстановки (NONE, FINE, BAD, LITTLE): ");
            String s;
            while(true) {
                try {
                    s = getValidString();
                    f = Furnish.valueOf(s.toUpperCase());
                    return f;
                } catch (IllegalArgumentException e) {
                    console.println("Значение может быть только NONE, FINE, BAD или LITTLE. Повторите ввод: ");
                }
            }
        } catch (IOException e) {
            console.printErr("Ошибка чтения");
            return null;
        }
    }
}
