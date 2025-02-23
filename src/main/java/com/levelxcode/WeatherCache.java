package com.levelxcode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages caching of weather data with LRU (least recently used) eviction and TTL (time to live).
 */
public class WeatherCache {
    private final int maxSize;
    private final LinkedHashMap<String, WeatherData> cache;

    /**
     * Initialize cache with maximum size
     * @param maxSize Maximum number of cities to cache
     */
    public WeatherCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<String, WeatherData>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, WeatherData> eldest) {
                return size() > maxSize;
            }
        };
    }

    /**
     * Get cached weather data if valid
     * @param cityName City to retrieve data for
     * @return WeatherData or null if invalid/expired
     */
    public synchronized WeatherData get(String cityName) {
        WeatherData data = cache.get(cityName);
        if (data != null && !data.isValid()) {
            cache.remove(cityName);
            return null;
        }
        return data;
    }

    /**
     * Store weather data in cache
     * @param cityName City to store data for
     * @param data WeatherData to store
     */
    public synchronized void put(String cityName, WeatherData data) {
        cache.put(cityName, data);
    }

    /**
     * Get list of currently cached cities
     */
    public synchronized List<String> getCachedCities() {
        return new ArrayList<>(cache.keySet());
    }
}
