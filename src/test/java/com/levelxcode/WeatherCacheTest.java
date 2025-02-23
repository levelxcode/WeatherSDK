package com.levelxcode;

import com.levelxcode.WeatherCache;
import com.levelxcode.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeatherCache functionality
 */
class WeatherCacheTest {
    private WeatherCache cache;
    private WeatherData testData;

    @BeforeEach
    void setUp() {
        // Initialize cache with maximum size of 2 entries
        cache = new WeatherCache(2);
        testData = new WeatherData();
        testData.setTimestamp(System.currentTimeMillis());
    }

    /**
     * Tests basic cache storage and retrieval functionality
     * - Stores data in cache
     * - Verifies data can be retrieved
     * - Checks cache size remains within limits
     */
    @Test
    void shouldStoreAndRetrieveData() {
        cache.put("london", testData);
        assertNotNull(cache.get("london"), "Cached data should be retrievable");
        assertEquals(1, cache.getCachedCities().size(), "Cache should contain exactly one entry");
    }

    /**
     * Tests LRU (Least Recently Used) eviction policy
     * - Adds three entries to size-2 cache
     * - Verifies oldest entry is evicted
     * - Checks remaining entries are correct
     */
    @Test
    void shouldEvictOldestEntryWhenLimitExceeded() {
        // Add three entries to test eviction
        cache.put("city1", testData);
        cache.put("city2", testData);
        cache.put("city3", testData); // Should trigger eviction

        // Verify cache size and contents
        assertEquals(2, cache.getCachedCities().size(), "Cache should maintain maximum size");
        assertNull(cache.get("city1"), "Oldest entry should be evicted");
        assertNotNull(cache.get("city2"), "Second entry should remain");
        assertNotNull(cache.get("city3"), "Newest entry should remain");
    }

    /**
     * Tests Time-To-Live (TTL) validation
     * - Creates expired weather data
     * - Verifies cache removes expired entries
     * - Checks cache returns null for expired data
     */
    @Test
    void shouldInvalidateExpiredEntries() {
        // Create data expired 11 minutes ago
        WeatherData oldData = new WeatherData();
        oldData.setTimestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(11));

        cache.put("expired", oldData);
        assertNull(cache.get("expired"), "Expired data should be removed from cache");
    }
}
