package com.thatwaz.weathercast.model.application



import android.app.Application
import com.thatwaz.weathercast.di.AppComponent
import com.thatwaz.weathercast.di.AppModule
import com.thatwaz.weathercast.di.DaggerAppComponent

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


    }
}





//import android.app.Application
//import com.thatwaz.weathercast.di.AppComponent
//import com.thatwaz.weathercast.di.AppModule
//import com.thatwaz.weathercast.di.DaggerAppComponent
//
//class WeatherCastApplication : Application() {
//
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
//    }
//}




//class WeatherCastApplication : Application() {
//
//    companion object {
//        lateinit var instance: WeatherCastApplication
//            private set
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//    }
//}
