package com.example.bookmemoapp;

public class Weather {
    int lat;
    int ion;
    double temprature;
    int cloudy;
    int humidity;
    String main;
    String city;

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public void setIon(int ion) {
        this.ion = ion;
    }

    public void setTemprature(double t) {
        this.temprature = t;
    }

    public void setCloudy(int cloudy) {
        this.cloudy = cloudy;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getLat() {
        return lat;
    }

    public int getIon() {
        return ion;
    }

    public double getTemprature() {
        return temprature;
    }

    public int getCloudy() {
        return cloudy;
    }

    public String getCity() {
        return city;
    }
}
