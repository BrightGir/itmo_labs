package ru.bright.model;


import java.io.Serializable;

/**
 * Класс типа обстановки квартиры
 */
public enum Furnish implements Comparable<Furnish>, Serializable {
    NONE,
    BAD,
    LITTLE,
    FINE;
}