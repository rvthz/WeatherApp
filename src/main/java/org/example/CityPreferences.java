import java.util.prefs.Preferences;

public class CityPreferences {
    private static final String LAST_CITY_KEY = "last_city";
    private static final String LAST_LATITUDE_KEY = "last_latitude";
    private static final String LAST_LONGITUDE_KEY = "last_longitude";

    public static void saveLastCity(String city, double latitude, double longitude) {
        Preferences prefs = Preferences.userRoot().node("com.example.app");
        prefs.put(LAST_CITY_KEY, city);
        prefs.putDouble(LAST_LATITUDE_KEY, latitude);
        prefs.putDouble(LAST_LONGITUDE_KEY, longitude);
    }

    public static String getLastCity() {
        Preferences prefs = Preferences.userRoot().node("com.example.app");
        return prefs.get(LAST_CITY_KEY, "");
    }

    public static double getLastLatitude() {
        Preferences prefs = Preferences.userRoot().node("com.example.app");
        return prefs.getDouble(LAST_LATITUDE_KEY, 0.0);
    }

    public static double getLastLongitude() {
        Preferences prefs = Preferences.userRoot().node("com.example.app");
        return prefs.getDouble(LAST_LONGITUDE_KEY, 0.0);
    }
}
