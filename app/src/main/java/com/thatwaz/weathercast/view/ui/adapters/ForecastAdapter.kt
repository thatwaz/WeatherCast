package com.thatwaz.weathercast.view.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thatwaz.weathercast.databinding.ItemForecastBinding
import com.thatwaz.weathercast.databinding.ItemHourlyBinding
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem

class ForecastAdapter :
    ListAdapter<WeatherItem, ForecastAdapter.ForecastViewHolder>(ForecastAdapter.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapter.ForecastViewHolder {
        return ForecastAdapter.ForecastViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val current = getItem(position)
        val prevItem = if (position > 0) getItem(position - 1) else null

        // Check if the date has changed compared to the previous item
//        val showDate = prevItem == null || !areDatesEqual(prevItem.dt, current.dt)

        holder.bind(current)
    }


    class ForecastViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(weatherItem: WeatherItem) {

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