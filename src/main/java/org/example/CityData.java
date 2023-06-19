package org.example;



public class CityData {
    private String country, cityName;
    private double lat, lon;

    public CityData(String cityName, String country, double lat, double lon) {
        this.country = country;
        this.cityName = cityName;
        this.lat = lat;
        this.lon = lon;
    }

    public String getCountry() {
        return country;
    }

    public String getCityName() {
        return cityName;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
