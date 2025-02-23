package com.levelxcode;

/**
 * Custom exception for SDK-related errors.
 */
public class WeatherSDKException extends Exception {
    public WeatherSDKException(String message) {
        super(message);
    }
}
