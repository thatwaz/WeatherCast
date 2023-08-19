package com.thatwaz.weathercast.view.ui.adapters

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.ItemForecastBinding
import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
import com.thatwaz.weathercast.utils.ConversionUtil
import com.thatwaz.weathercast.utils.ConversionUtil.breakTextIntoLines
import com.thatwaz.weathercast.utils.ConversionUtil.capitalizeWords
import com.thatwaz.weathercast.utils.ConversionUtil.convertUnixTimestampToFormattedDate
import com.thatwaz.weathercast.utils.ConversionUtil.kelvinToFahrenheit
import com.thatwaz.weathercast.utils.WeatherIconUtil
import java.util.*

class ForecastAdapter :
    ListAdapter<DailyForecast, ForecastAdapter.ForecastViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val context = parent.context
        return ForecastViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ),
            context
        )
    }


    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ForecastViewHolder(
        private val binding: ItemForecastBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyForecast: DailyForecast) {
            val forecastDescriptionToCaps = dailyForecast.weatherDescription.capitalizeWords()
            val weatherIcon = dailyForecast.weatherIcon
            val displayIcon = WeatherIconUtil.getForecastWeatherIconResource(weatherIcon)
            val chanceOfRainPercentage = dailyForecast.chanceOfRain
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = inputDateFormat.format(Date())
            val date = inputDateFormat.parse(dailyForecast.date)
            if (dailyForecast.date == currentDate) {

                binding.cvForecastDetails.visibility = View.GONE
            } else {
                binding.cvForecastDetails.visibility = View.VISIBLE


                val lowTemperatureFahrenheit = kelvinToFahrenheit(dailyForecast.lowTemperature)
                val windDirectionDegrees = dailyForecast.windDeg // Use windDeg property
                val formattedWindDirection = ConversionUtil.getWindDirection(windDirectionDegrees)
                val windSpeed = dailyForecast.windSpeed.toInt()
                val feelsLikeTemperature = kelvinToFahrenheit(dailyForecast.feelsLikeTemperature)
                val highTemperatureFahrenheit = kelvinToFahrenheit(dailyForecast.highTemperature)
                //Refactor the rest of the strings like below
                val formattedHighTemperature = context.getString(R.string.temperature_high,
                    highTemperatureFahrenheit.toString())

                binding.apply {
                    tvForecastDescription.text = breakTextIntoLines(forecastDescriptionToCaps, 18)
                    tvForecastDate.text = convertUnixTimestampToFormattedDate(date)
                    tvTemperatureHigh.text = formattedHighTemperature
                    tvTemperatureLow.text = "$lowTemperatureFahrenheit°F"
                    tvForecastChanceOfRain.text = "${chanceOfRainPercentage.toInt()}%"
                    ivForecastIcon.setImageResource(displayIcon)
                    tvForecastHumidity.text = "${dailyForecast.humidity}%"
                    tvForecastFeelsLikeTemperature.text = "$feelsLikeTemperature°F"
                    tvForecastWind.text = "$formattedWindDirection $windSpeed mph"
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DailyForecast>() {
            override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: DailyForecast,
                newItem: DailyForecast
            ): Boolean {
                return oldItem == newItem
            }
        }

    }
}
