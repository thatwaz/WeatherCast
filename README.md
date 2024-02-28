# WeatherCast

WeatherCast is a comprehensive weather app for Android devices, built using the **MVVM architectural pattern**. The app showcases current weather conditions, periodic forecasts in 3-hour intervals, and daily forecasts for up to 5 days. It provides a seamless user experience through a clean user interface, accompanied by weather icons and beautiful cloud backdrops.





## Features

- **MVVM Architecture**: The app is built following the Model-View-ViewModel pattern.
- **Dagger**: Dependency injection is managed using Dagger.
- **Fade-in Splash Screen**: Introduces users to the app with a subtle fade-in effect.
- **Three Fragments**:
   - **Current Weather**: Displays the present weather with a corresponding weather icon against a backdrop of cloud images.
   - **Periodic Weather**: Showcases weather forecasts in 3-hour intervals for the next 5 days.
   - **Forecast**: Presents a daily weather snapshot for the next 5 days.
- **U.S. Weather Data Representation**: Converts European weather data to familiar U.S. standards, such as Fahrenheit and miles.
- **Location-Based Results**: Displays weather data based on the user's location with an option to refresh.
- **Data Caching**: Uses Room for efficient data caching.

## Technologies Used

- Kotlin
- Android Studio
- MVVM Pattern
- Dagger for Dependency Injection
- Android Navigation
- Retrofit
- Room
- Dexter (for permissions)
- Google Play Services (for location)

## Getting Started

1. Clone the repository: 
2. Open the project in Android Studio.
3. Obtain an API key from [OpenWeatherMap](https://openweathermap.org/).
4. Add your API key to the `local.properties` file:

Replace `your_api_key_goes_here` with your actual API key.

5. Sync the Gradle files and run the app.

## Permissions

- **Location**: Fetches weather data specific to the user's location.
- **Internet**: Enables network requests to the weather API.

## Credits

- **Cloud Images**: Captured and owned by me.
- **Weather Icons**: Sourced from Iconfinder. For more details on the licensing and usage terms, please visit [Iconfinder's License Overview](https://support.iconfinder.com/en/articles/18233-license-overview).

- **Weather API**: Data obtained from [OpenWeatherMap](https://openweathermap.org/).

## Memory Leak Detection

The app integrates LeakCanary for memory leak detection during development. This feature is not required; feel free to remove the LeakCanary dependency from the `build.gradle` module.

## Feedback & Contributions

Feel free to open issues or pull requests if you find bugs or want to contribute.




