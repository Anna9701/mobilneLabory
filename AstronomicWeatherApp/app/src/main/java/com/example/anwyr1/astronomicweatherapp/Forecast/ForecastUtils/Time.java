package com.example.anwyr1.astronomicweatherapp.Forecast.ForecastUtils;

import com.example.anwyr1.astronomicweatherapp.DateUtil;

import java.io.Serializable;

/**
 * Created by anwyr1 on 29/04/2018.
 */

public class Time implements Serializable {
    private String from;
    private String to;

    public Time(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return DateUtil.changeTimezoneToCurrent(from);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return DateUtil.changeTimezoneToCurrent(to);
    }

    public void setTo(String to) {
        this.to = to;
    }
}
