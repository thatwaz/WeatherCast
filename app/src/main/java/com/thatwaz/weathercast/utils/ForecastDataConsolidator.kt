package com.thatwaz.weathercast.utils

import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.utils.ConversionUtil.convertRainToPercentage

object ForecastDataConsolidator {

    fun consolidate(forecastResponse: ForecastResponse): List<DailyForecast> {
        val dailyForecasts = mutableListOf<DailyForecast>()

        // Group the forecast items by day
        val groupedForecasts = forecastResponse.list.groupBy { forecastItem ->
            forecastItem.dtTxt.substringBefore(" ") // Extract the date part
        }

        // Calculate high and low temperatures, weather description, and other properties for each day
        for ((date, forecasts) in groupedForecasts) {
            val highTemp = forecasts.maxByOrNull { it.main.tempMax }?.main?.tempMax ?: 0.0
            val lowTemp = forecasts.minByOrNull { it.main.tempMin }?.main?.tempMin ?: 0.0
            val weatherDescription =
                forecasts.firstOrNull()?.weather?.getOrNull(0)?.description ?: ""
            val weatherIcon = forecasts.firstOrNull()?.weather?.getOrNull(0)?.icon ?: ""
            val rainForecast = forecasts.firstOrNull()?.rain
            val chanceOfRain = convertRainToPercentage(rainForecast) // You'll need to move the convertRainToPercentage method or pass a lambda for it
            val humidity = forecasts.firstOrNull()?.main?.humidity ?: 0
            val feelsLikeTemperature = forecasts.firstOrNull()?.main?.feelsLike ?: 0.0
            val windSpeed = forecasts.firstOrNull()?.wind?.speed ?: 0.0
            val windDeg = forecasts.firstOrNull()?.wind?.deg ?: 0

            dailyForecasts.add(
                DailyForecast(
                    date = date,
                    highTemperature = highTemp,
                    lowTemperature = lowTemp,
                    weatherDescription = weatherDescription,
                    weatherIcon = weatherIcon,
                    chanceOfRain = chanceOfRain.toDouble(),
                    humidity = humidity,
                    feelsLikeTemperature = feelsLikeTemperature,
                    windSpeed = windSpeed,
                    windDeg = windDeg,
                    cityName = forecastResponse.city.name
                )
            )
        }
        return dailyForecasts
    }
}
