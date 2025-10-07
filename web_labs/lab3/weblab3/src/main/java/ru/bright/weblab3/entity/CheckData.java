package ru.bright.weblab3.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "results")
@Getter
@Setter
public class CheckData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double x;

    private double y;

    private double r;

    private boolean hit;

    private double executionTime;

    private Long clientTimestamp;

    private String clientTimeZone;

    public CheckData() {

    }

    public String getFormattedTimestamp() {
        if (clientTimestamp != null && clientTimeZone != null) {
            try {
                Instant instant = Instant.ofEpochMilli(clientTimestamp);
                ZonedDateTime clientZoned = instant.atZone(ZoneId.of(clientTimeZone));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                return clientZoned.format(formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getFormattedExecutionTime() {
        return String.format("%.4f", executionTime);
    }

    public String getFormattedX() {
        return String.format("%.3f", x);
    }

    public String getFormattedY() {
        return String.format("%.3f", y);
    }

    public String getFormattedR() {
        return String.format("%.3f", r);
    }



}
