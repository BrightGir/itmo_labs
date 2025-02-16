package ru.bright.model;

import java.util.Objects;

/**
 * Класс координат
 */
public class Coordinates implements Cloneable, Comparable<Coordinates> {
    private float x; //Значение поля должно быть больше -699
    private Integer y; //Поле не может быть null


    public Coordinates(float x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }


    @Override
    public Coordinates clone() {
        return new Coordinates(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates cord = (Coordinates) o;
        return x == cord.x && y.equals(cord.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public int compareTo(Coordinates o) {
        int xComparison = x < o.x ? -1 : x == o.x ? 0 : 1;
        if(xComparison != 0) return xComparison;
        int yComparison = y < o.y ? -1 : y == o.y ? 0 : 1;
        return yComparison;
    }
}