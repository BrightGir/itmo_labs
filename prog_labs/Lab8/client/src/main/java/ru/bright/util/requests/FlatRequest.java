package ru.bright.util.requests;

import ru.bright.model.Coordinates;
import ru.bright.model.Flat;
import ru.bright.model.Furnish;
import ru.bright.model.House;
import ru.bright.util.Console;

import java.io.IOException;


/**
 * Класс для запроса объекта {@link Flat}
 */
public class FlatRequest extends Request {

    public FlatRequest(Console console) {
        super(console);
    }
/*
    private Long id; //Поле не может быть null,
    // Значение поля должно быть больше 0,
    // Значение этого поля должно быть уникальным,
    // Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float area; //Значение поля должно быть больше 0
    private int numberOfRooms; //Значение поля должно быть больше 0
    private Float timeToMetroOnFoot; //Значение поля должно быть больше 0
    private Boolean centralHeating; //Поле не может быть null
    private Furnish furnish; //Поле может быть null
    private House house; //Поле не может быть null
*/

    /**
     * Формирует объект {@link Flat} на основе пользовательского ввода
     * @return
     */
    public Flat create() {
        try {
            console.println("Введите название квартиры: ");
            String name = getValidString();
            console.println("Введите координаты квартиры");
            Coordinates coordinates = new CoordinatesRequest(console).create();
            if(coordinates == null) return null;
            console.println("Введите размер квартиры: ");
            float area = getValidFloat(x -> x > 0, "Значение должно быть больше 0");
            console.println("Введите количество комнат: ");
            int numberOfRooms = getValidInt(x -> x > 0, "Значение должно быть больше 0");
            console.println("Введите время, за которое можно дойти до ближайшего метро от квартиры: ");
            float timeToMetroOnFoot = getValidFloat(x -> x > 0, "Значение должно быть больше 0");
            console.println("Есть ли в квартире центральное отопление? (true | false)");
            boolean centralHeating = getValidBoolean();
            console.println("Какая обстановка у вашей квартиры?");
            Furnish furnish = new FurnishRequest(console).create();
            if(furnish == null) return null;
            console.println("В каком доме находится ваша квартира? ");
            House house = new HouseRequest(console).create();
            if(house == null) return null;
            Flat flat = new Flat(name,coordinates,area,numberOfRooms,timeToMetroOnFoot,
                    centralHeating,furnish,house);
            return flat;
        } catch (IOException e) {
            console.printErr("Ошибка чтения");
            return null;
        }
    }


}
