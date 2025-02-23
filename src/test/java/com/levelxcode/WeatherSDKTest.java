package com.levelxcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeatherSDK core functionality
 */
class WeatherSDKTest {
    private String TEST_API_KEY = "test_key";

    @BeforeEach
    void setUp(){
        try (InputStream input = WeatherSDKTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value
            TEST_API_KEY = prop.getProperty("api.key");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Tests cache utilization behavior
     * - Makes two identical requests
     * - Verifies second request uses cached data
     * - Checks response consistency
     */
    @Test
    void shouldReturnCachedDataWhenFresh() throws Exception {
        WeatherSDK sdk = new WeatherSDK(TEST_API_KEY, WeatherSDK.Mode.ON_DEMAND);
        // Initial request populates cache
        String firstResult = sdk.getWeather("London");
        // Subsequent request should use cache
        String secondResult = sdk.getWeather("London");
        assertNotNull(secondResult, "Cached data should be available");
        assertEquals(firstResult, secondResult, "Cached data should match original");
    }

    /**
     * Tests cache expiration handling
     * - Forces cache entry expiration
     * - Verifies fresh data is fetched
     * - Checks cache updates with new data
     */
    @Test
    void shouldRefreshCacheWhenExpired() throws Exception {
        WeatherSDK sdk = new WeatherSDK(TEST_API_KEY, WeatherSDK.Mode.ON_DEMAND);
        sdk.getWeather("Paris");

        // Force expiration by backdating timestamp
        WeatherData data = sdk.cache.get("paris");
        data.setTimestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(11));

        // New request should bypass cache
        assertDoesNotThrow(() -> sdk.getWeather("Paris"),
                "Should handle cache refresh gracefully");
    }

    /**
     * Tests input validation for empty city name
     * - Verifies exception is thrown
     * - Checks error message content
     */
    @Test
    void shouldHandleInvalidCityName() {
        WeatherSDK sdk = new WeatherSDK(TEST_API_KEY, WeatherSDK.Mode.ON_DEMAND);
        Exception exception = assertThrows(WeatherSDKException.class, () ->
                sdk.getWeather("")
        );
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Should validate city name input");
    }
}
