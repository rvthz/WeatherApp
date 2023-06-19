package org.example;

public class ForecastData {
    private long timestamp;
    private double minTemperature;
    private double maxTemperature;
    private String iconCode;

    private String date;

    public ForecastData(long timestamp, double minTemperature, double maxTemperature, String iconCode) {
        this.timestamp = timestamp;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.iconCode = iconCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public String getIconCode() {
        return iconCode;
    }
}