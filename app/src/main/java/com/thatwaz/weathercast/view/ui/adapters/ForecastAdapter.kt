package com.thatwaz.weathercast.view.ui.adapters

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thatwaz.weathercast.databinding.ItemForecastBinding
import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
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
        return ForecastViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ForecastViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {



        fun bind(dailyForecast: DailyForecast) {
            val forecastDescriptionToCaps = dailyForecast.weatherDescription.capitalizeWords()
            val weatherIcon = dailyForecast.weatherIcon
            val displayIcon = WeatherIconUtil.getForecastWeatherIconResource(weatherIcon)
            val chanceOfRainPercentage = dailyForecast.chanceOfRain
            val highTemperatureFahrenheit = kelvinToFahrenheit(dailyForecast.highTemperature)
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputDateFormat.parse(dailyForecast.date)
            val lowTemperatureFahrenheit = kelvinToFahrenheit(dailyForecast.lowTemperature)
            val windDirectionDegrees = dailyForecast.windDeg // Use windDeg property
            val formattedWindDirection = ConversionUtil.getWindDirection(windDirectionDegrees)
            val windSpeed = dailyForecast.windSpeed.toInt()


            val feelsLikeTemperature = kelvinToFahrenheit(dailyForecast.feelsLikeTemperature)

            binding.apply {
                tvForecastDescription.text = breakTextIntoLines(forecastDescriptionToCaps,18)
                tvForecastDate.text = convertUnixTimestampToFormattedDate(date)
                tvTemperatureHigh.text = "$highTemperatureFahrenheit°F"
                tvTemperatureLow.text = "$lowTemperatureFahrenheit°F"
                tvForecastChanceOfRain.text = "${chanceOfRainPercentage.toInt()}%"
                ivForecastIcon.setImageResource(displayIcon)
                tvForecastHumidity.text = "${dailyForecast.humidity}%"
                tvForecastFeelsLikeTemperature.text = "$feelsLikeTemperature°F"
                tvForecastWind.text = "$formattedWindDirection $windSpeed mph"
            }
        }
    }




    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DailyForecast>() {
            override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
                // Compare items based on the date property
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
                // Compare items based on their contents
                return oldItem == newItem
            }
        }

    }
}





//class ForecastAdapter :
//    ListAdapter<DailyForecast, ForecastAdapter.ForecastViewHolder>(DiffCallback) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
//        return ForecastViewHolder(
//            ItemForecastBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
//        val current = getItem(position)
////        val prevItem = if (position > 0) getItem(position - 1) else null
//
//        // Check if the date has changed compared to the previous item
////        val showDate = prevItem == null || !areDatesEqual(prevItem.dt, current.dt)
//
//        holder.bind(current)
//    }
//
//
//    class ForecastViewHolder(private val binding: ItemForecastBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//            fun bind(dailyForecast: DailyForecast) {
////                val unixTimestamp = weatherItem.dt
////                val date = ConversionUtil.convertUnixTimestampToFormattedDate(unixTimestamp)
//                binding.apply {
//                    tvForecastDescription.text = "Lorem ipsum dolor sit amet. Et autem ullam sit magnam quia qui tempora obcaecati aut necessitatibus dolores. Non tempora dolore ut blanditiis laborum aut molestias illum in corporis mollitia eum quia asperiores sed voluptas vitae eos esse omnis."
////                    tvForecastDate.text = date
//                }
//
//            }
//        }
//
//
//    companion object {
//        private val DiffCallback = object : DiffUtil.ItemCallback<WeatherItem>() {
//            override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
//                return oldItem.dt == newItem.dt
//            }
//
//            override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}