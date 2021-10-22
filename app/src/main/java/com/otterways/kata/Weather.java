package com.otterways.kata;

public class Weather {
    private String city;
    private double temp;
    private String weather;
    private String iconWeather;

    public Weather() {
        city = "";
        temp = 0f;
        weather = "";
        iconWeather = "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getIconWeather() {
        return iconWeather;
    }

    public void setIconWeather(String iconWeather) {
        this.iconWeather = iconWeather;
    }
}
