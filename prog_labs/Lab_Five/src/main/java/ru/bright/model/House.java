package ru.bright.model;


import java.util.Objects;


/**
 * Класс дома
 */
public class House implements Cloneable, Comparable<House> {

    private String name; //Поле не может быть null
    private Integer year; //Максимальное значение поля: 117, Значение поля должно быть больше 0
    private long numberOfFlatsOnFloor; //Значение поля должно быть больше 0

    public House(String name, Integer year, long numberOfFlatsOnFloor) {
        this.name = name;
        this.year = year;
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;
    }

    public House() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public long getNumberOfFlatsOnFloor() {
        return numberOfFlatsOnFloor;
    }

    public void setNumberOfFlatsOnFloor(long numberOfFlatsOnFloor) {
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;
    }

    @Override
    public House clone() {
        return new House(name, year, numberOfFlatsOnFloor);
    }

    @Override
    public String toString() {
        return "House{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", numberOfFlatsOnFloor=" + numberOfFlatsOnFloor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return numberOfFlatsOnFloor == house.numberOfFlatsOnFloor
                && name.equals(house.name) && year.equals(house.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, numberOfFlatsOnFloor);
    }

    @Override
    public int compareTo(House o) {
        int nameComparison = name.compareTo(o.name);
        if(nameComparison != 0) return nameComparison;
        int yearComparison = year.compareTo(o.year);
        if(yearComparison != 0) return yearComparison;
        return Long.compare(numberOfFlatsOnFloor, o.numberOfFlatsOnFloor);
    }
}
