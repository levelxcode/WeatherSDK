package com.levelxcode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main SDK class for accessing weather data from OpenWeatherMap API
 */
public class WeatherSDK {
    private final String apiKey;
    private final Mode mode;
    public final WeatherCache cache;
    private final ApiClient apiClient;
    private ScheduledExecutorService scheduler;

    /**
     * Operation modes for the SDK:
     * - ON_DEMAND: Fetch fresh data only when requested
     * - POLLING: Periodically update all cached cities
     */
    public enum Mode {
        ON_DEMAND, POLLING
    }

    /**
     * Initialize the SDK with API key and operation mode
     * @param apiKey OpenWeatherMap API key
     * @param mode Operation mode (ON_DEMAND/POLLING)
     */
    public WeatherSDK(String apiKey, Mode mode) {
        this.apiKey = apiKey;
        this.mode = mode;
        this.cache = new WeatherCache(10);
        this.apiClient = new ApiClient(apiKey);

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }

    /**
     * Start background polling thread for automatic updates
     */
    private void startPolling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule updates every 10 minutes
        scheduler.scheduleAtFixedRate(this::refreshAllCachedCities, 0, 10, TimeUnit.MINUTES);
    }

    /**
     * Refresh weather data for all cached cities
     */
    private void refreshAllCachedCities() {
        List<String> cities;
        synchronized (cache) {
            cities = new ArrayList<>(cache.getCachedCities());
        }
        for (String city : cities) {
            try {
                WeatherData data = apiClient.fetchWeatherData(city);
                cache.put(city, data);
            } catch (WeatherSDKException e) {
                System.err.println("Failed to refresh city " + city + ": " + e.getMessage());
            }
        }
    }

    /**
     * Get weather data for a specific city
     * @param cityName Name of the city to query
     * @return JSON string with weather data
     * @throws WeatherSDKException If any error occurs during data retrieval
     */
    public String getWeather(String cityName) throws WeatherSDKException {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new WeatherSDKException("City name cannot be null or empty");
        }

        String normalizedCityName = cityName.trim().toLowerCase();
        WeatherData cachedData = cache.get(normalizedCityName);

        // Return cached data if still valid
        if (cachedData != null && cachedData.isValid()) {
            return cachedData.toJson();
        }

        // Fetch fresh data and update cache
        WeatherData freshData = apiClient.fetchWeatherData(cityName);
        cache.put(normalizedCityName, freshData);
        return freshData.toJson();
    }

    /**
     * Shutdown the SDK
     */
    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
