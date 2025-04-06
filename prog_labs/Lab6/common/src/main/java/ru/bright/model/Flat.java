package ru.bright.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.Math.max;


/**
 * Класс квартиры
 */
public class Flat implements Cloneable, Comparable<Flat>, Serializable {

    private static long lastId = 0;

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

    public Flat(String name, Coordinates coordinates, float area, int numberOfRooms, Float timeToMetroOnFoot, Boolean centralHeating, Furnish furnish, House house) {
        //this.id = new Random().nextLong(Long.MAX_VALUE);
        lastId++;
        this.id = lastId;
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.timeToMetroOnFoot = timeToMetroOnFoot;
        this.centralHeating = centralHeating;
        this.creationDate = java.time.ZonedDateTime.now();
        this.furnish = furnish;
        this.house = house;
    }

    public Flat(Long id, String name, Coordinates coordinates, float area, int numberOfRooms, Float timeToMetroOnFoot, Boolean centralHeating,
                ZonedDateTime creationDate,
                Furnish furnish, House house) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.timeToMetroOnFoot = timeToMetroOnFoot;
        this.centralHeating = centralHeating;
        this.creationDate = creationDate;
        this.furnish = furnish;
        this.house = house;
    }

    public Flat() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Float getTimeToMetroOnFoot() {
        return timeToMetroOnFoot;
    }

    public void setTimeToMetroOnFoot(Float timeToMetroOnFoot) {
        this.timeToMetroOnFoot = timeToMetroOnFoot;
    }

    public Boolean getCentralHeating() {
        return centralHeating;
    }

    public void setCentralHeating(Boolean centralHeating) {
        this.centralHeating = centralHeating;
    }

    public Furnish getFurnish() {
        return furnish;
    }

    public void setFurnish(Furnish furnish) {
        this.furnish = furnish;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    @Override
    public Flat clone() {
        return new Flat(name,coordinates.clone(),area,numberOfRooms,
                timeToMetroOnFoot,centralHeating,furnish,house.clone());
    }

    @Override
    public String toString() {
        return "Flat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                ", area=" + area +
                ", numberOfRooms=" + numberOfRooms +
                ", timeToMetroOnFoot=" + timeToMetroOnFoot +
                ", centralHeating=" + centralHeating +
                ", furnish=" + furnish +
                ", house=" + house +
                '}';
    }


    @Override
    public int compareTo(Flat o) {

        int areaComparison = Float.compare(area,o.area);
        if (areaComparison != 0) return areaComparison;

        int numberOfRoomsComparison = Integer.compare(this.numberOfRooms, o.numberOfRooms);
        if (numberOfRoomsComparison != 0) return numberOfRoomsComparison;

        int timeToMetroOnFootComparison = Float.compare(this.timeToMetroOnFoot, o.timeToMetroOnFoot);
        if (timeToMetroOnFootComparison != 0) return timeToMetroOnFootComparison;

        int coordinatesComparison = this.coordinates.compareTo(o.coordinates);
        if (coordinatesComparison != 0) return coordinatesComparison;

        int centralHeatingComparison = Boolean.compare(this.centralHeating, o.centralHeating);
        if (centralHeatingComparison != 0) return centralHeatingComparison;

        int furnishComparison = this.furnish.compareTo(o.furnish);
        if (furnishComparison != 0) return furnishComparison;

        int houseComparison = this.house.compareTo(o.house);
        if (houseComparison != 0) return houseComparison;

        int creationDateComparison = this.creationDate.compareTo(o.creationDate);
        if (creationDateComparison != 0) return creationDateComparison;

        int nameComparison = this.name.compareTo(o.name);
        if (nameComparison != 0) return nameComparison;
        return 0;
    }

    /**
     * Возвращает первое неиспользованное айди
     * @return ID
     */
  //  private long generateId() {
  //     CollectionManager manager = new CollectionManager();
  //     for(long i = 1; i <= 100000000;i ++) {
  //         if(manager.getById(i) == null) {
  //             return i;
  //         }
  //     }
  //     return 0;
  // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flat flat = (Flat) o;
        return area == flat.area && numberOfRooms == flat.numberOfRooms &&
                id.equals(flat.id) && name.equals(flat.name) &&
                coordinates.equals(flat.coordinates) &&
                creationDate.equals(flat.creationDate) &&
                Objects.equals(timeToMetroOnFoot, flat.timeToMetroOnFoot) &&
                centralHeating.equals(flat.centralHeating) &&
                furnish == flat.furnish && house.equals(flat.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, area, numberOfRooms, timeToMetroOnFoot, centralHeating, furnish, house);
    }
}

