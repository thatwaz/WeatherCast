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
            val date = ConversionUtil.convertUnixTimestampToRelativeDate(unixTimestamp)
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




