package ru.bright.lab;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AreaResponse implements Serializable {

    private final String x, y, r;
    private final boolean isHit;
    private final String currentTime;
    private final String executionTime;

    public AreaResponse(String x, String y, String r, boolean isHit, long passedTimeNano) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.isHit = isHit;
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
        this.executionTime = String.format("%.4f c", passedTimeNano / 1e9);
    }
}
