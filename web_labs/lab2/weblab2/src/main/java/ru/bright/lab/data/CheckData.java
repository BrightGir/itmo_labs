package ru.bright.lab.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class CheckData implements Serializable {

    private String x;
    private String y;
    private String r;
    private boolean hit;
    private String currentTime;
    private String executionTime;

    public CheckData(double x, double y, double r, boolean hit, long executionTime) {
        this.x = String.format("%.3f",x);
        this.y = String.format("%.3f",y);
        this.r = String.format("%.3f",r);
        this.hit = hit;
        this.currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.executionTime = String.format("%.6f", (double)executionTime/1e9);
    }

}