package com.levelxcode;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Handles HTTP requests to the OpenWeatherMap API and response parsing.
 */
public class ApiClient {
    private final String apiKey;
    private final HttpClient httpClient;



    /**
     * Initialize API client with API key
     * @param apiKey OpenWeatherMap API key
     */
    public ApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Fetch weather data from API
     * @param cityName Name of the city to query
     * @return Parsed WeatherData object
     * @throws WeatherSDKException If API request fails
     */
    public WeatherData fetchWeatherData(String cityName) throws WeatherSDKException {
        String encodedCity = cityName.replace(" ", "%20");
        String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s",
                encodedCity, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                handleErrorResponse(response);
            }

            return parseWeatherData(response.body());
        } catch (IOException | InterruptedException e) {
            throw new WeatherSDKException("Network error: " + e.getMessage());
        }
    }

    /**
     * Handle non-200 HTTP responses
     * @param response HTTP response object
     * @throws WeatherSDKException Appropriate error message
     */
    private void handleErrorResponse(HttpResponse<String> response) throws WeatherSDKException {
        int code = response.statusCode();
        String message = switch (code) {
            case 401 -> "Invalid API key";
            case 404 -> "City not found";
            case 429 -> "Too Many Requests";
            case 500, 502, 503, 504 -> "Server error";
            default -> "Unexpected response code: " + code;
        };
        throw new WeatherSDKException(message);
    }

    /**
     * Parse API response JSON into WeatherData object
     * @param jsonResponse Raw JSON response from API
     * @return Parsed WeatherData
     * @throws WeatherSDKException If parsing fails
     */
    private WeatherData parseWeatherData(String jsonResponse) throws WeatherSDKException {
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray weatherArray = root.getJSONArray("weather");
            if (weatherArray.isEmpty()) {
                throw new WeatherSDKException("No weather data found");
            }

            JSONObject weather = weatherArray.getJSONObject(0);
            JSONObject main = root.getJSONObject("main");
            JSONObject wind = root.getJSONObject("wind");
            JSONObject sys = root.getJSONObject("sys");

            WeatherData data = new WeatherData();
            data.setMainWeather(weather.getString("main"));
            data.setDescription(weather.getString("description"));
            data.setTemp(main.getDouble("temp"));
            data.setFeelsLike(main.getDouble("feels_like"));
            data.setVisibility(root.getInt("visibility"));
            data.setWindSpeed(wind.getDouble("speed"));
            data.setDatetime(root.getLong("dt"));
            data.setSunrise(sys.getLong("sunrise"));
            data.setSunset(sys.getLong("sunset"));
            data.setTimezone(root.getInt("timezone"));
            data.setCityName(root.getString("name"));
            data.setTimestamp(System.currentTimeMillis());

            return data;
        } catch (Exception e) {
            throw new WeatherSDKException("Failed to parse weather data: " + e.getMessage());
        }
    }
}
