package com.thatwaz.weathercast.repository

//class ForecastRepository() {
//    private val weatherService: WeatherService
//
//    init {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(ApiConfig.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        weatherService = retrofit.create(WeatherService::class.java)
//    }
//
//
//    suspend fun getForecastData(
//        appId: String,
//        latitude: Double,
//        longitude: Double,
//    ): Response<Forecast> {
//        return weatherService.getWeatherForecast(latitude, longitude, appId)
//    }
//}