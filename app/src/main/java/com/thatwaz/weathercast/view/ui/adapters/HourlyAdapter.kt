package com.thatwaz.weathercast.view.ui.adapters

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thatwaz.weathercast.databinding.ItemHourlyBinding


import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.utils.ConversionUtil
import com.thatwaz.weathercast.utils.WeatherIconUtil
import java.util.*

class HourlyAdapter :
    ListAdapter<WeatherItem, HourlyAdapter.HourlyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        return HourlyViewHolder(
            ItemHourlyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val current = getItem(position)
        val prevItem = if (position > 0) getItem(position - 1) else null

        // Check if the date has changed compared to the previous item
        val showDate = prevItem == null || !areDatesEqual(prevItem.dt, current.dt)

        holder.bind(current, showDate)
    }

    class HourlyViewHolder(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(weatherItem: WeatherItem, showDate: Boolean) {
            val unixTimestamp = weatherItem.dt // Access the Unix timestamp from the WeatherItem
            val timeRange = ConversionUtil.convertUnixTimestampToTimeRange(unixTimestamp)
            val date = ConversionUtil.convertUnixTimestampToDate(unixTimestamp)
            val weatherCondition = weatherItem.weather[0].description
            val weatherIcon = weatherItem.weather[0].icon
            val displayIcon = WeatherIconUtil.getWeatherIconResource(weatherIcon)
            val chanceOfRainPercentage = ConversionUtil.convertRainToPercentage(weatherItem.rain)

            binding.apply {
                if (showDate) {
                    clDate.visibility = View.VISIBLE
                    tvDate.text = date
                } else {
                    clDate.visibility = View.GONE
                }

                tvTime.text = timeRange
                tvWeatherCondition.text = ConversionUtil.breakTextIntoLines(weatherCondition, 18)
                val temperatureInFahrenheit =
                    ConversionUtil.kelvinToFahrenheit(weatherItem.main.temp)
                tvTemperature.text = "${temperatureInFahrenheit}°F"
                ivWeatherIcon.setImageResource(displayIcon)
                binding.tvChanceOfRain.text = "$chanceOfRainPercentage%"

            }
        }
    }

    private fun areDatesEqual(timestamp1: Long, timestamp2: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date1 = dateFormat.format(Date(timestamp1 * 1000))
        val date2 = dateFormat.format(Date(timestamp2 * 1000))
        return date1 == date2
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<WeatherItem>() {
            override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
                return oldItem.dt == newItem.dt
            }

            override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}


//class HourlyForecastAdapter :
//    ListAdapter<WeatherItem, HourlyForecastAdapter.HourlyForecastViewHolder>(DiffCallback) {
//    private var lastDisplayedDate: String? = null
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
//        Log.i("POOP", "onCreateViewHolder")
//        return HourlyForecastViewHolder(
//            ItemForecastBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
//        val current = getItem(position)
//        Log.i("POOP", "onBindViewHolder - position: $position")
//        holder.bind(current)
//    }
//
//    class HourlyForecastViewHolder(private val binding: ItemForecastBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//
//        fun bind(weatherItem: WeatherItem) {
//            Log.i("POOP", "bind - weatherItem: $weatherItem")
//
//            val unixTimestamp = weatherItem.dt // Access the Unix timestamp from the WeatherItem
//            val timeRange = ConversionUtil.convertUnixTimestampToTimeRange(unixTimestamp)
//            val date = ConversionUtil.convertUnixTimestampToDate(unixTimestamp.toLong())
//            binding.apply {
//                if (lastDisplayedDate != date) {
//                    tvDate.visibility = View.VISIBLE
//                    tvDate.text = date
//                } else {
//                    tvDate.visibility = View.GONE
//                }
//
//                tvTime.text = timeRange
//                tvWeatherCondition.text = "${weatherItem.weather[0].description}"
//                val temperatureInFahrenheit =
//                    ConversionUtil.kelvinToFahrenheit(weatherItem.main.temp)
//                tvTemperature.text = "${temperatureInFahrenheit}°F"
//
//                lastDisplayedDate = date
//            }


//            binding.apply {
////                tvTime.text = "${weatherItem.dtTxt}"
//                tvTime.text = timeRange
//                tvWeatherCondition.text = "Condition: ${weatherItem.weather[0].description}"
//                val temperatureInFahrenheit = ConversionUtil.kelvinToFahrenheit(weatherItem.main.temp)
//                tvTemperature.text = "${temperatureInFahrenheit}°F"
////                tvTemperature.text = "${weatherItem.main.temp}°C"
//            }
//        }
//    }


//class HourlyForecastAdapter :
//    ListAdapter<WeatherItem, HourlyForecastAdapter.HourlyForecastViewHolder>(DiffCallback) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
//        return HourlyForecastViewHolder(
//            ItemForecastBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
//        val current = getItem(position)
//
//        holder.bind(current)
//    }
//
//    class HourlyForecastViewHolder(private val binding: ItemForecastBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(weatherItem: WeatherItem) {
//            binding.apply {
//                tvTime.text = "Time: ${weatherItem.dtTxt}"
//                tvWeatherCondition.text = "Condition: ${weatherItem.weather[0].description}"
//                tvTemperature.text = "Temperature: ${weatherItem.main.temp}°C"
//            }
//        }
//    }
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

//class HourlyForecastAdapter :
//    ListAdapter<WeatherItem, HourlyForecastAdapter.HourlyForecastViewHolder>(DiffCallback) {
//
//    // ViewHolder for each forecast item
//    class HourlyForecastViewHolder(private val binding: ItemForecastBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(weatherItem: WeatherItem) {
//            // Bind the data from the weatherItem to the views in the layout
//            // For example:
////            binding.tvTime.text = "Time: ${weatherItem.dtTxt}"
//            binding.tvTime.text = "Time: ${weatherItem.dtTxt}"
//            Log.i("POOP", "time is ${weatherItem.dtTxt}")
//            binding.tvWeatherCondition.text = "Condition: ${weatherItem.weather[0].description}"
//            binding.tvTemperature.text = "Temperature: ${weatherItem.main.temp}°C"
//        }
//    }
//
//    // DiffCallback to calculate list differences
//    object DiffCallback : DiffUtil.ItemCallback<WeatherItem>() {
//        override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
//            return oldItem.dt == newItem.dt // Check if items represent the same object
//        }
//
//        override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
//            return oldItem == newItem // Check if item contents are the same
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemForecastBinding.inflate(inflater, parent, false)
//        return HourlyForecastViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
//        val weatherItem = getItem(position)
//        Log.i("POOP", "onBindViewHolder called for position $position")
//        Log.i("POOP","Weather item is $weatherItem")
//        holder.bind(weatherItem)
//    }
//}


