package com.thatwaz.weathercast.view.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thatwaz.weathercast.databinding.ItemForecastBinding
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.utils.ConversionUtil
import com.thatwaz.weathercast.utils.ConversionUtil.convertUnixTimestampToTimeWithAMPM

class HourlyForecastAdapter :
    ListAdapter<WeatherItem, HourlyForecastAdapter.HourlyForecastViewHolder>(DiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        Log.i("POOP", "onCreateViewHolder")
        return HourlyForecastViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val current = getItem(position)
        Log.i("POOP", "onBindViewHolder - position: $position")
        holder.bind(current)
    }

    class HourlyForecastViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var lastDisplayedDate: String? = null

        fun bind(weatherItem: WeatherItem) {
            Log.i("POOP", "bind - weatherItem: $weatherItem")

            val unixTimestamp = weatherItem.dt // Access the Unix timestamp from the WeatherItem
            val timeRange = ConversionUtil.convertUnixTimestampToTimeRange(unixTimestamp)
            val date = ConversionUtil.convertUnixTimestampToDate(unixTimestamp.toLong())
            binding.apply {
                if (lastDisplayedDate != date) {
                    tvDate.visibility = View.VISIBLE
                    tvDate.text = date
                } else {
                    tvDate.visibility = View.GONE
                }

                tvTime.text = timeRange
                tvWeatherCondition.text = "Condition: ${weatherItem.weather[0].description}"
                val temperatureInFahrenheit =
                    ConversionUtil.kelvinToFahrenheit(weatherItem.main.temp)
                tvTemperature.text = "${temperatureInFahrenheit}°F"

                lastDisplayedDate = date
            }


//            binding.apply {
////                tvTime.text = "${weatherItem.dtTxt}"
//                tvTime.text = timeRange
//                tvWeatherCondition.text = "Condition: ${weatherItem.weather[0].description}"
//                val temperatureInFahrenheit = ConversionUtil.kelvinToFahrenheit(weatherItem.main.temp)
//                tvTemperature.text = "${temperatureInFahrenheit}°F"
////                tvTemperature.text = "${weatherItem.main.temp}°C"
//            }
        }
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


