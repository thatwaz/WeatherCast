package com.thatwaz.weathercast.model.application



import android.app.Application
import androidx.room.Room
import com.thatwaz.weathercast.di.AppComponent
import com.thatwaz.weathercast.di.AppModule
import com.thatwaz.weathercast.di.DaggerAppComponent
import com.thatwaz.weathercast.model.database.WeatherDatabase



class WeatherCastApplication : Application() {
    // Create a Dagger component variable
    lateinit var appComponent: AppComponent

    companion object {
        lateinit var instance: WeatherCastApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize the Dagger component using DaggerAppComponent.builder()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        // You don't need to build the Room database instance here anymore
        // The WeatherDatabase instance is now provided by Dagger through the DatabaseModule

        // Other initialization code
    }
}

//class WeatherCastApplication : Application() {
//    // Create a Dagger component variable
//    lateinit var appComponent: AppComponent
//
//    companion object {
//        lateinit var instance: WeatherCastApplication
//            private set
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//
//        // Initialize the Dagger component using DaggerAppComponent.builder()
//        appComponent = DaggerAppComponent.builder()
//            .appModule(AppModule(this))
//            .build()
//
//        // Build the Room database instance with fallbackToDestructiveMigration
//        val weatherDatabase = Room.databaseBuilder(
//            applicationContext,
//            WeatherDatabase::class.java,
//            "weather_database"
//        )
//            .fallbackToDestructiveMigration() // Enable destructive migration
//            .build()
//
//        // Other initialization code
//    }
//}


//class WeatherCastApplication : Application() {
//    // Create a Dagger component variable
//    lateinit var appComponent: AppComponent
//
//    companion object {
//        lateinit var instance: WeatherCastApplication
//            private set
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//
//        // Initialize the Dagger component using DaggerAppComponent.builder()
//        appComponent = DaggerAppComponent.builder()
//            .appModule(AppModule(this))
//            .build()
//
//
//    }
//}

