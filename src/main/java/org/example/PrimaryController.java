package org.example;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import org.example.ForecastData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PrimaryController {

    @FXML
    private ImageView weatherImage;

    @FXML
    private Button searchButton;

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

    private String language = "pl";

    @FXML
    private ToggleButton plButton;

    @FXML
    private Label feelsLikeLabel;

    @FXML
    private Label sunriseLabel;

    @FXML
    private Label sunsetLabel;

    @FXML
    private Label windDirectionLabel;

    @FXML
    private Label uvIndexLabel;

    @FXML
    private ToggleButton enButton;

    private static final String API_KEY = "f45cb2c8afa6167eddcf6236a0a59e31";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather";

    Map<String, List<ForecastData>> groupedData = new HashMap<>();
    boolean isSearchMode = false;

    private List<CityData> cityList; // Lista zawierająca dane miast

    public void setCityList(List<CityData> cityList) {
        this.cityList = cityList;
    }


    @FXML

    private void initialize() throws IOException {

        searchField.setVisible(false);
        searchButton.setText("Szukaj");
        //plButton.setOnAction(event -> changeLanguage("pl"));
        //enButton.setOnAction(event -> changeLanguage("en"));
        String lastCity = CityPreferences.getLastCity();
        double lastLatitude = CityPreferences.getLastLatitude();
        double lastLongitude = CityPreferences.getLastLongitude();
        searchButton.setOnAction(event -> {
            if (!isSearchMode) {
                searchButton.setText("Zatwierdź");
                searchField.setVisible(true);
                isSearchMode = true;
            } else {
                searchButton.setText("Szukaj");
                searchField.setVisible(false);
                isSearchMode = false;
                String searchText = searchField.getText();
                showDialog(searchText);
            }
        });

        double latitude = 50.02599;
        double longitude = 20.96406;
        initSearch(latitude, longitude); //inicjalizacja



    }



    private void initSearch(double lat, double lon) throws IOException {
        //czas rzeczywisty
        String weatherData = fetchDataFromApi(String.valueOf(lat), String.valueOf(lon), "pl");
        parseWeatherData(weatherData);

        //prognoza 5 dni
        String forecastData = getForecastData(String.valueOf(lat), String.valueOf(lon));
        List<ForecastData> forecastList = parseForecastData(forecastData);
        processForecastList(forecastList);
        List<List<ForecastData>> groupedForecastData = groupForecastDataByDate(forecastList);
        processGroupedForecastData(groupedForecastData);
    }

    private void changeLanguage(String newLanguage) { //nie działa
        if (!newLanguage.equals(language)) {
            language = newLanguage;

            try {
                ResourceBundle bundle = ResourceBundle.getBundle("org.example.resources.labels", new Locale(language));

                // Set labels and buttons text
                temperatureLabel.setText(bundle.getString("temperature_label"));
                weatherDescriptionLabel.setText(bundle.getString("weather_description_label"));
                cityNameLabel.setText(bundle.getString("city_name_label"));
                minTempLabel.setText(bundle.getString("min_temp_label"));
                maxTempLabel.setText(bundle.getString("max_temp_label"));
                pressureLabel.setText(bundle.getString("pressure_label"));
                humidityLabel.setText(bundle.getString("humidity_label"));
                windSpeedLabel.setText(bundle.getString("wind_speed_label"));
                searchButton.setText(bundle.getString("search_button"));
                searchField.setPromptText(bundle.getString("search_field_prompt"));

                // Set toggle buttons text
                plButton.setText(bundle.getString("pl_button"));
                enButton.setText(bundle.getString("en_button"));

                double latitude = 50.02599;
                double longitude = 20.96406;
                initSearch(latitude, longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //popup z opcjami wyszukiawnia
    private void showDialog(String searchText) {
        List<CityData> cityList = geocodeLocation(searchText);

        Dialog<CityData> dialog = new Dialog<>();
        dialog.setTitle("Szukaj");
        dialog.setHeaderText("Wybierz lokalizację");

        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(cancelButtonType);

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        int maxItems = Math.min(cityList.size(), 5); // Ograniczenie do maksymalnie 5 (tyle mozna odebrac przez api)

        //tworzenie listy w pop upie
        for (int i = 0; i < maxItems; i++) {
            CityData cityData = cityList.get(i);

            HBox cityBox = new HBox();
            cityBox.setAlignment(Pos.CENTER_LEFT);
            cityBox.setSpacing(10);

            ImageView flagImageView = new ImageView();
            flagImageView.setFitHeight(16);
            flagImageView.setFitWidth(16);
            flagImageView.setImage(getFlagImage(cityData.getCountry()));

            Label cityNameLabel = new Label(cityData.getCityName());
            cityNameLabel.setFont(Font.font("Arial", 20));

            Label coordinatesLabel = new Label(String.format("Lat: %.2f, Lon: %.2f", cityData.getLat(), cityData.getLon()));
            coordinatesLabel.setFont(Font.font("Arial", 15));

            cityBox.getChildren().addAll(flagImageView, cityNameLabel, coordinatesLabel);
            cityBox.setCursor(Cursor.HAND);

            cityBox.setOnMouseEntered(event -> cityBox.setStyle("-fx-background-color: #EFEFEF;"));
            cityBox.setOnMouseExited(event -> cityBox.setStyle("-fx-background-color: transparent;"));

            cityBox.setOnMouseClicked(event -> {
                dialog.setResult(cityData);
                dialog.close();
            });

            vbox.getChildren().add(cityBox);
        }

        dialog.getDialogPane().setContent(vbox);
        dialog.initStyle(StageStyle.UNDECORATED);

        Optional<CityData> result = dialog.showAndWait();
        result.ifPresent(selectedCity -> {
            try {
                initSearch(selectedCity.getLat(), selectedCity.getLon());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }



    //nazwa na koordynaty
    private List<CityData> geocodeLocation(String locationName) {
        List<CityData> cityList = new ArrayList<>();

        try {

            String encodedLocation = URLEncoder.encode(locationName, StandardCharsets.UTF_8);

            String apiUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + encodedLocation +
                    "&limit=10&appid=" + API_KEY;

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JSONArray jsonArray = new JSONArray(httpResponse.body());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String cityName = jsonObject.getString("name");
                String countryCode = jsonObject.getString("country");
                double latitude = jsonObject.getDouble("lat");
                double longitude = jsonObject.getDouble("lon");

                CityData cityData = new CityData(cityName, countryCode, latitude, longitude);
                cityList.add(cityData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cityList;
    }


    //flagi
    private Image getFlagImage(String countryCode) {
        try {
            String flagUrl = "https://flagcdn.com/w80/" + countryCode.toLowerCase() + ".png";
            InputStream inputStream = new URL(flagUrl).openStream();
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    //prognoza
    private String getForecastData(String latitude, String longitude) throws IOException {
        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&lang=" + language;
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

    //dane czasu rzeczywistego
    private void parseWeatherData(String weatherData) {
        try {
            JSONObject json = new JSONObject(weatherData);
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            JSONObject sys = json.getJSONObject("sys");

            double temperature = main.getDouble("temp");
            double minTemperature = main.getDouble("temp_min");
            double maxTemperature = main.getDouble("temp_max");
            double pressure = main.getDouble("pressure");
            int humidity = main.getInt("humidity");
            double windSpeed = wind.getDouble("speed");
            String iconCode = weather.getString("icon");
            double feelsLike = main.getDouble("feels_like");
            double windDirection = wind.getDouble("deg");

            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            double temperatureCelsius = temperature - 273.15;
            double feelsLikeCelsius = feelsLike - 273.15;


            temperatureLabel.setText(Math.round(temperatureCelsius) + "°C");
            minTempLabel.setText(decimalFormat.format(minTemperature - 273.15) + "°C");
            maxTempLabel.setText(decimalFormat.format(maxTemperature - 273.15) + "°C");
            pressureLabel.setText("Ciśnienie " + pressure + " hPa");
            humidityLabel.setText("Wilgotność " + humidity + "%");
            windSpeedLabel.setText("Wiatr " + decimalFormat.format(windSpeed) + " m/s");
            feelsLikeLabel.setText("Odczuwalna " + decimalFormat.format(feelsLikeCelsius) + "°C");


            String weatherDescription = weather.getString("description");
            weatherDescriptionLabel.setText(weatherDescription);

            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
            Image image = new Image(iconUrl);
            weatherImage.setImage(image);

            String cityName = json.getString("name");


            cityNameLabel.setText(cityName);

            String windDirectionText = getWindDirectionText(windDirection);
            windDirectionLabel.setText("Kierunek wiatru " + windDirectionText);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //kierunek wiatru konwertowany ze stopni
    private String getWindDirectionText(double windDirection) {
        String[] directions = {"Północny", "Północno-Wschodni", "Wschodni", "Południowo-Wschodni", "Południowy",
                "Południowo-Zachodni", "Zachodni", "Północno-Zachodni"};
        int index = (int) Math.round(((windDirection % 360) / 45));
        return directions[index % 8];
    }


    //dane prognozy
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

    //konwersja timestampów
    private void processForecastList(List<ForecastData> forecastList) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM");
        for (ForecastData forecast : forecastList) {
            LocalDateTime timestamp = LocalDateTime.ofEpochSecond(forecast.getTimestamp(), 0, java.time.ZoneOffset.UTC);
            LocalDate date = timestamp.toLocalDate();
            String formattedDate = date.format(dateFormatter);

            forecast.setDate(formattedDate);
        }

    }


    //grupowanie po datach - każda lista to inny dzien
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

    //przetwarzanie pogrupowanych dat na dane do prognozy
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

    //wypisywanie prognozy
    private void setTemperatureFields(int dayIndex, double minTemperature, double maxTemperature, String date, String iconCode) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

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
