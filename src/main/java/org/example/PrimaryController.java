package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.ForecastData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PrimaryController {

    @FXML
    private ImageView weatherImage;

    @FXML
    private TextField searchField;

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
    private Label day1date;

    @FXML
    private Label day1min;

    @FXML
    private Label day1max;

    @FXML
    private ImageView day1icon;

    @FXML
    private Label day2date;

    @FXML
    private Label day2min;

    @FXML
    private Label day2max;

    @FXML
    private ImageView day2icon;

    @FXML
    private Label day3date;

    @FXML
    private Label day3min;

    @FXML
    private Label day3max;

    @FXML
    private ImageView day3icon;

    @FXML
    private Label day4date;

    @FXML
    private Label day4min;

    @FXML
    private Label day4max;

    @FXML
    private ImageView day4icon;

    @FXML
    private Label day5date;

    @FXML
    private Label day5min;

    @FXML
    private Label day5max;

    @FXML
    private ImageView day5icon;

    private static final String API_KEY = "f45cb2c8afa6167eddcf6236a0a59e31";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather";

    Map<String, List<ForecastData>> groupedData = new HashMap<>();

    @FXML

    private void initialize() {
        try {
            String latitude = "50.02599";
            String longitude = "20.96406";
            String weatherData = fetchDataFromApi(latitude, longitude, "pl");
            parseWeatherData(weatherData);

            String forecastData = getForecastData(latitude, longitude);
            List<ForecastData> forecastList = parseForecastData(forecastData);
            processForecastList(forecastList);

            List<List<ForecastData>> groupedForecastData = groupForecastDataByDate(forecastList);
            processGroupedForecastData(groupedForecastData);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private String getForecastData(String latitude, String longitude) throws IOException {
        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
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
            double pressure = main.getDouble("pressure");
            int humidity = main.getInt("humidity");
            double windSpeed = wind.getDouble("speed");
            String iconCode = weather.getString("icon");

            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            double temperatureCelsius = temperature - 273.15;

            // Set the labels with weather data
            temperatureLabel.setText(Math.round(temperatureCelsius) + "°C");


            minTempLabel.setText(decimalFormat.format(minTemperature - 273.15) + "°C");
            maxTempLabel.setText(decimalFormat.format(maxTemperature - 273.15) + "°C");

            pressureLabel.setText("Ciśnienie " + pressure + " hPa");
            humidityLabel.setText("Wilgotność " + humidity + "%");
            windSpeedLabel.setText("Wiatr " + decimalFormat.format(windSpeed) + " m/s");

            // Set the weather description label
            String weatherDescription = weather.getString("description");
            weatherDescriptionLabel.setText(weatherDescription);

            // Load weather icon
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
            Image image = new Image(iconUrl);
            weatherImage.setImage(image);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ForecastData> parseForecastData(String forecastData) {
        List<ForecastData> forecastList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(forecastData);
            JSONArray forecasts = jsonObject.getJSONArray("list");

            for (int i = 0; i < forecasts.length(); i++) {
                JSONObject forecast = forecasts.getJSONObject(i);

                long timestamp = forecast.getLong("dt");
                JSONObject main = forecast.getJSONObject("main");
                double minTemperature = main.getDouble("temp_min");
                double maxTemperature = main.getDouble("temp_max");
                JSONArray weatherArray = forecast.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String iconCode = weather.getString("icon");

                ForecastData forecastDataObj = new ForecastData(timestamp, minTemperature, maxTemperature, iconCode);
                forecastList.add(forecastDataObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return forecastList;
    }

    private void processForecastList(List<ForecastData> forecastList) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM");
        for (ForecastData forecast : forecastList) {
            LocalDateTime timestamp = LocalDateTime.ofEpochSecond(forecast.getTimestamp(), 0, java.time.ZoneOffset.UTC);
            LocalDate date = timestamp.toLocalDate();
            String formattedDate = date.format(dateFormatter);

            forecast.setDate(formattedDate);
        }

    }



    private List<List<ForecastData>> groupForecastDataByDate(List<ForecastData> forecastList) {
        Map<String, List<ForecastData>> groupedData = new HashMap<>();

        for (ForecastData forecast : forecastList) {
            String date = forecast.getDate();
            List<ForecastData> dataList = groupedData.getOrDefault(date, new ArrayList<>());
            dataList.add(forecast);
            groupedData.put(date, dataList);
        }

        // Sort the forecast lists by date
        for (List<ForecastData> dataList : groupedData.values()) {
            dataList.sort(Comparator.comparingLong(ForecastData::getTimestamp));
        }

        return new ArrayList<>(groupedData.values());
    }

    private void processGroupedForecastData(List<List<ForecastData>> groupedForecastData) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM");
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < Math.min(groupedForecastData.size(), 5); i++) {
            LocalDate date = currentDate.plusDays(i);

            List<ForecastData> group = null;
            for (List<ForecastData> forecastGroup : groupedForecastData) {
                LocalDateTime timestamp = LocalDateTime.ofEpochSecond(forecastGroup.get(0).getTimestamp(), 0, java.time.ZoneOffset.UTC);
                LocalDate forecastDate = timestamp.toLocalDate();
                if (forecastDate.equals(date)) {
                    group = forecastGroup;
                    break;
                }
            }

            if (group != null) {
                double maxTemperature = Double.MIN_VALUE;
                double minTemperature = Double.MAX_VALUE;
                String maxIconCode = "";

                for (ForecastData forecast : group) {
                    if (forecast.getMaxTemperature() > maxTemperature) {
                        maxTemperature = forecast.getMaxTemperature();
                        maxIconCode = forecast.getIconCode();
                    }
                    if (forecast.getMinTemperature() < minTemperature) {
                        minTemperature = forecast.getMinTemperature();
                    }
                }

                String formattedDate = date.format(dateFormatter);

                // Set values to corresponding fields
                setTemperatureFields(i + 1, minTemperature, maxTemperature, formattedDate, maxIconCode);
            }
        }
    }


    private void setTemperatureFields(int dayIndex, double minTemperature, double maxTemperature, String date, String iconCode) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + ".png";

        switch (dayIndex) {
            case 1:
                day1min.setText(decimalFormat.format(minTemperature - 273.15) + "°C");
                day1max.setText(decimalFormat.format(maxTemperature - 273.15) + "°C");
                day1date.setText(date);
                loadImageIntoImageView(day1icon, iconUrl);
                break;
            case 2:
                day2min.setText(decimalFormat.format(minTemperature - 273.15) + "°C");
                day2max.setText(decimalFormat.format(maxTemperature - 273.15) + "°C");
                day2date.setText(date);
                loadImageIntoImageView(day2icon, iconUrl);
                break;
            case 3:
                day3min.setText(decimalFormat.format(minTemperature - 273.15) + "°C");
                day3max.setText(decimalFormat.format(maxTemperature - 273.15) + "°C");
                day3date.setText(date);
                loadImageIntoImageView(day3icon, iconUrl);
                break;
            case 4:
                day4min.setText(decimalFormat.format(minTemperature - 273.15) + "°C");
                day4max.setText(decimalFormat.format(maxTemperature - 273.15) + "°C");
                day4date.setText(date);
                loadImageIntoImageView(day4icon, iconUrl);
                break;
            case 5:
                day5min.setText(decimalFormat.format(minTemperature - 273.15) + "°C");
                day5max.setText(decimalFormat.format(maxTemperature - 273.15) + "°C");
                day5date.setText(date);
                loadImageIntoImageView(day5icon, iconUrl);
                break;
            default:
                break;
        }
    }

    private void loadImageIntoImageView(ImageView imageView, String imageUrl) {
        Image image = new Image(imageUrl);
        imageView.setImage(image);
    }



}
