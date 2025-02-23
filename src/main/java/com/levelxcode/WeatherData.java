package com.levelxcode;

import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

/**
 * Represents weather data and handles JSON conversion.
 */

public class WeatherData {
    private String mainWeather;
    private String description;
    private double temp;
    private double feelsLike;
    private int visibility;
    private double windSpeed;
    private long datetime;
    private long sunrise;
    private long sunset;
    private int timezone;
    private String cityName;
    private long timestamp;

    /**
     * Check if data is still valid (not older than 10 minutes)
     */
    public boolean isValid() {
        return (System.currentTimeMillis() - timestamp) < TimeUnit.MINUTES.toMillis(10);
    }

    /**
     * Convert weather data to formatted JSON string
     * @return Pretty-printed JSON
     * @throws WeatherSDKException If JSON conversion fails
     */
    public String toJson() throws WeatherSDKException {
        try {
            JSONObject json = new JSONObject();
            JSONObject weather = new JSONObject();
            weather.put("main", mainWeather);
            weather.put("description", description);

            JSONObject temperature = new JSONObject();
            temperature.put("temp", temp);
            temperature.put("feels_like", feelsLike);

            JSONObject wind = new JSONObject();
            wind.put("speed", windSpeed);

            JSONObject sys = new JSONObject();
            sys.put("sunrise", sunrise);
            sys.put("sunset", sunset);

            json.put("weather", weather);
            json.put("temperature", temperature);
            json.put("visibility", visibility);
            json.put("wind", wind);
            json.put("datetime", datetime);
            json.put("sys", sys);
            json.put("timezone", timezone);
            json.put("name", cityName);

            return json.toString(2);
        } catch (Exception e) {
            throw new WeatherSDKException("Failed to serialize weather data to JSON: " + e.getMessage());
        }
    }

    public void setMainWeather(String mainWeather) { this.mainWeather = mainWeather; }
    public void setDescription(String description) { this.description = description; }
    public void setTemp(double temp) { this.temp = temp; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
    public void setVisibility(int visibility) { this.visibility = visibility; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public void setDatetime(long datetime) { this.datetime = datetime; }
    public void setSunrise(long sunrise) { this.sunrise = sunrise; }
    public void setSunset(long sunset) { this.sunset = sunset; }
    public void setTimezone(int timezone) { this.timezone = timezone; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getMainWeather() {return mainWeather;}
    public String getDescription() {return description;}
    public double getTemp() {return temp;}
    public double getFeelsLike() {return feelsLike;}
    public int getVisibility() {return visibility;}
    public double getWindSpeed() {return windSpeed;}
    public long getDatetime() {return datetime;}
    public long getSunrise() {return sunrise;}
    public long getSunset() {return sunset;}
    public int getTimezone() {return timezone;}
    public String getCityName() {return cityName;}
    public long getTimestamp() {return timestamp;}
}
