package com.osmium.weatherrepoter2017.model;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by OSMiUM on 16/07/2017.
 */

public class Weather {
    private int id;
    private String city;
    private String date;
    private String weather;
    private String temp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
