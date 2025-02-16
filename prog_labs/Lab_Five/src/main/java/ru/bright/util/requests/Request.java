package ru.bright.util.requests;

import ru.bright.util.Console;

import java.io.IOException;
import java.util.function.Predicate;


/**
 * Абстрактный класс для формирования запросов на ввод данных от пользователя.
 */
public abstract class Request {

    /**
     * Объект консоли для взаимодействия с пользователем
     */
    protected Console console;

    public Request(Console console) {
        this.console = console;
    }


    /**
     * Метод для интерактивного взаимодействия с пользователем и формирования объекта
     * @return Созданный объект.
     */
    public abstract Object create();


    /**
     * Считывает непустую строку
     * @return Считанная строка
     * @throws IOException
     */
    protected String getValidString() throws IOException {
        String s;
        s = console.readLine();
        while(s == null || s.isEmpty()) {
            console.println("Значение не может быть пустым. Повторите ввод: ");
            s = console.readLine();
        }
        return s;
    }

    /**
     * Считывает вещественное число, удовлетворяющее условию
     * Выводит соответствующее сообщение в случае несоблюдения условия
     * @return Считанное число
     * @throws IOException
     */
    protected float getValidFloat(Predicate<Float> condition, String conditionMessage) throws IOException {
        float x;
        while(true) {
            try {
                x = Float.parseFloat(getValidString());
                if(condition != null && condition.test(x)) {
                    break;
                } else {
                    console.println(conditionMessage);
                }
            } catch (NumberFormatException e) {
                console.println("Значение должно быть вещественным числом. Повторите ввод: ");
            } catch (NullPointerException e) {
                return 0;
            }
        }
        return x;
    }

    /**
     * Считывает вещественное число
     * @return Считанное число
     * @throws IOException
     */
    protected float getValidFloat() throws IOException {
        float x;
    //    console.println("Введите вещественное число: ");

        while(true) {
            try {
                x = Float.parseFloat(getValidString());
                break;
            } catch (NumberFormatException e) {
                console.println("Значение должно быть вещественным числом. Повторите ввод: ");
            }
        }
        return x;
    }

    /**
     * Считывает целое число, удовлетворяющее условию
     * Выводит соответствующее сообщение в случае несоблюдения условия
     * @return Считанное число
     * @throws IOException
     */
    protected long getValidLong(Predicate<Long> condition, String conditionMessage) throws IOException {
        long x;
        while(true) {
            try {
                x = Long.parseLong(getValidString());
                if(condition != null && condition.test(x)) {
                    break;
                } else {
                    console.println(conditionMessage);
                }
            } catch (NumberFormatException e) {
                console.println("Значение должно быть целым числом. Повторите ввод: ");
            }
        }
        return x;
    }


    /**
     * Считывает целое число, удовлетворяющее условию
     * Выводит соответствующее сообщение в случае несоблюдения условия
     * @return Считанное число
     * @throws IOException
     */
    protected int getValidInt(Predicate<Integer> condition, String conditionMessage) throws IOException {
        int x;
        while(true) {
            try {
                x = Integer.parseInt(getValidString());
                if(condition != null && condition.test(x)) {
                    return x;
                } else {
                    console.println(conditionMessage);
                }
            } catch (NumberFormatException e) {
                console.println("Значение должно быть целым числом. Повторите ввод: ");
            }
        }
    }

    /**
     * Считывает целое число
     * @return Считанное число
     * @throws IOException
     */
    protected int getValidInt() throws IOException {
        int x;
        while(true) {
            try {
                x = Integer.parseInt(getValidString());
                break;
            } catch (NumberFormatException e) {
                console.println("Значение должно быть целым числом. Повторите ввод: ");
            }
        }
        return x;
    }

    /**
     * Считывает логическое значение
     * @return Считанное логическое значение
     * @throws IOException
     */
    protected boolean getValidBoolean() throws IOException {
        String x;
        while(true) {
            x = getValidString();
            if (x.equals("true")) {
                return true;
            } else if (x.equals("false")) {
                return false;
            } else {
                console.println("Значение должно быть логическим значением (true или false). Повторите ввод: ");
            }
        }
    }
}
