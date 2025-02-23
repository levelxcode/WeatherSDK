package com.levelxcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests with real API (requires valid API key)
 */
class IntegrationTest {
    private String REAL_API_KEY = "api_key";

    @BeforeEach
    void setUp(){
        try (InputStream input = WeatherSDKTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value
            REAL_API_KEY = prop.getProperty("api.key");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Tests end-to-end functionality with real API
     * - Verifies successful API communication
     * - Checks response structure validity
     * - Validates basic data presence
     */
    @Test
    void shouldFetchRealData() throws Exception {
        WeatherSDK sdk = new WeatherSDK(REAL_API_KEY, WeatherSDK.Mode.ON_DEMAND);
        String result = sdk.getWeather("London");

        assertNotNull(result, "Should receive API response");
        assertTrue(result.contains("\"weather\""),
                "Response should contain weather section");
        assertTrue(result.contains("\"temperature\""),
                "Response should contain temperature data");
    }
}
