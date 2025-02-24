# Java Weather SDK for OpenWeatherMap API

WeatherSDK is a Java library that simplifies interaction with the 
OpenWeatherMap API. It allows you to easily fetch weather data for 
a specific city and get the results in JSON format. The SDK handles 
the communication with the API, error handling, and caching of weather 
data for improved performance.

## Features

- ☀️ Fetch current weather data for a specific city.
- ⬇️ ON_DEMAND mode only fetches new data when requested.
- ⚡ POLLING mode automatically refreshes cached cities every 10 minutes
- 🗃️ LRU caching (10 cities max)
- ⏱️ Automatic cache invalidation (10 minutes TTL)
- 🛡️ Handles network and API errors

## Installation

### Maven `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.levelxcode</groupId>
    <artifactId>WeatherSDK</artifactId>
    <version>v1.0.0</version>
</dependency>
```

### Gradle (build.gradle):
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.levelxcode:WeatherSDK:v1.0.0'
}
```

## Quick Start
To use the SDK, create an instance of WeatherSDK with your OpenWeatherMap API key and the desired operation mode.
```java
public static void main(String[] args) {
    WeatherSDK sdk = new WeatherSDK("API_KEY", Mode.ON_DEMAND);
    System.out.println(sdk.getWeather("London"));
}
```

## Usage
### Initialization
```java
// On-Demand mode (fetch fresh data on each request)
WeatherSDK sdk = new WeatherSDK("YOUR_API_KEY", WeatherSDK.Mode.ON_DEMAND);

// Polling mode (auto-refresh cache data every 10 minutes)
WeatherSDK pollingSdk = new WeatherSDK("YOUR_API_KEY", WeatherSDK.Mode.POLLING);
```
### Retrieving Weather Data
```java
try {
    String weatherJson = sdk.getWeather("London");
    System.out.println(weatherJson);
} catch (WeatherSDKException e) {
    System.err.println("Error: " + e.getMessage());
}
```

### Remember to shutdown the SDK when done to stop the polling thread
```java
pollingSdk.shutdown();
```
### Example Response
```json
{
"weather": {
"main": "Clouds",
"description": "scattered clouds"
},
"temperature": {
"temp": 261.67,
"feels_like": 261.67
},
"visibility": 10000,
"wind": {
"speed": 3.1
},
"datetime": 1675744800,
"sys": {
"sunrise": 1675751262,
"sunset": 1675787560
},
"timezone": 3600,
"name": "London"
}
```
## Configuration
### Operation Modes

| Mode | Description                                         |
|----------|-----------------------------------------------------|
| ON_DEMAND | Fresh data is fetched on each request               |
| POLLING | Automatic background cache updates every 10 minutes |

### Error Handling

The SDK throws WeatherSDKException with clear error messages:
```java
try {
sdk.getWeather("InvalidCity123");
} catch (WeatherSDKException e) {
System.err.println("Error: " + e.getMessage());
// Example messages:
// - Invalid API key
// - City not found
// - Network error: Failed to connect
// - Empty city name
}
```
