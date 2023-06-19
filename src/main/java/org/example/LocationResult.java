package org.example;

public class LocationResult {
    private final String cityName;
    private final String countryCode;
    private final double latitude;
    private final double longitude;

    public LocationResult(String cityName, String countryCode, double latitude, double longitude) {
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return cityName + ", " + countryCode + " (Lat: " + latitude + ", Lon: " + longitude + ")";
    }
}
