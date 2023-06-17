package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class PrimaryController {

    @FXML
    private TextField latitudeField;

    @FXML
    private TextField longitudeField;

    @FXML
    private Button searchButton;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label weatherDescriptionLabel;

    @FXML
    private Label cityNameLabel;

    @FXML
    private Label minTempLabel;

    @FXML
    private Label maxTempLabel;

    @FXML
    private Label pressureLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label windSpeedLabel;

    @FXML
    private ImageView weatherImage;

    private static final String API_KEY = "f45cb2c8afa6167eddcf6236a0a59e31";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather";

    @FXML
    private void initialize() {
        searchButton.setOnAction(event -> {
            try {
                String latitude = "50.02599";
                String longitude = "20.96406";
                String weatherData = fetchDataFromApi(latitude, longitude, "pl"); // Default language is Polish (pl)
                parseWeatherData(weatherData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String fetchDataFromApi(String latitude, String longitude, String language) throws IOException {
        String urlString = API_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&lang=" + URLEncoder.encode(language, "UTF-8");
        StringBuilder result = new StringBuilder();

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString();
    }

    private void parseWeatherData(String weatherData) {
        try {
            JSONObject json = new JSONObject(weatherData);
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);

            double temperature = main.getDouble("temp");
            double minTemperature = main.getDouble("temp_min");
            double maxTemperature = main.getDouble("temp_max");
            int pressure = main.getInt("pressure");
            int humidity = main.getInt("humidity");
            double windSpeed = wind.getDouble("speed");
            String iconCode = weather.getString("icon");

            // Convert temperature from Kelvin to Celsius with 1 decimal place
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String temperatureCelsius = decimalFormat.format(temperature - 273.15);

            // Set the labels with weather data
            temperatureLabel.setText("Temperature: " + temperatureCelsius + "°C");
            minTempLabel.setText("Min Temp: " + decimalFormat.format(minTemperature - 273.15) + "°C");
            maxTempLabel.setText("Max Temp: " + decimalFormat.format(maxTemperature - 273.15) + "°C");
            pressureLabel.setText("Pressure: " + pressure + " hPa");
            humidityLabel.setText("Humidity: " + humidity + "%");
            windSpeedLabel.setText("Wind Speed: " + decimalFormat.format(windSpeed) + " m/s");

            // Set the weather description label
            String weatherDescription = weather.getString("description");
            weatherDescriptionLabel.setText("Weather: " + weatherDescription);

            // Load weather icon
            String iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";
            Image image = new Image(iconUrl);
            weatherImage.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
