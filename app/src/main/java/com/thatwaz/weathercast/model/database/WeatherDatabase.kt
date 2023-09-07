package com.thatwaz.weathercast.model.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.thatwaz.weathercast.model.database.dao.ForecastDao
import com.thatwaz.weathercast.model.database.dao.HourlyWeatherDao
import com.thatwaz.weathercast.model.database.dao.WeatherDataDao
import com.thatwaz.weathercast.model.database.entities.ForecastEntity
import com.thatwaz.weathercast.model.database.entities.HourlyWeatherEntity

@Database(entities = [WeatherDataEntity::class, HourlyWeatherEntity::class, ForecastEntity::class], version = 2, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDataDao
    abstract fun hourlyWeatherDao(): HourlyWeatherDao
    abstract fun forecastDao(): ForecastDao

//    companion object {
//        @Volatile
//        private var INSTANCE: WeatherDatabase? = null

//        fun getInstance(context: Context): WeatherDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    WeatherDatabase::class.java,
//                    "weather_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}

