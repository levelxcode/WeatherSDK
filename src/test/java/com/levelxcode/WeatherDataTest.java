package com.levelxcode;

import com.levelxcode.WeatherData;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeatherDataTest {

    /**
     * Tests JSON serialization functionality
     * - Creates populated WeatherData object
     * - Verifies JSON structure and content
     * - Checks required fields presence
     */
    @Test
    void shouldGenerateValidJson() throws Exception {
        WeatherData data = new WeatherData();
        data.setCityName("Berlin");
        data.setTemp(22.5);
        data.setVisibility(10000);

        String json = data.toJson();
        assertTrue(json.contains("\"name\": \"Berlin\""),
                "JSON should contain city name");
        assertTrue(json.contains("\"temp\": 22.5"),
                "JSON should contain temperature");
        assertTrue(json.contains("\"visibility\": 10000"),
                "JSON should contain visibility data");
    }

    /**
     * Tests TTL validation logic
     * - Creates expired data entry
     * - Verifies validity check fails
     * - Checks time threshold handling
     */
    @Test
    void shouldDetectExpiredData() {
        WeatherData data = new WeatherData();
        // Set timestamp 11 minutes in the past
        data.setTimestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(11));

        assertFalse(data.isValid(), "Data should be considered expired");
    }

}
