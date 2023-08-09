package com.thatwaz.weathercast.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.repository.FakeWeatherRepository
import com.thatwaz.weathercast.repository.WeatherRepository
import com.thatwaz.weathercast.utils.error.Resource
import kotlinx.coroutines.test.runBlockingTest

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

//@RunWith(AndroidJUnit4ClassRunner::class)
//class WeatherViewModelTest {
//
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var viewModel: WeatherViewModel
//
//    @Before
//    fun setUp() {
//        val fakeWeatherRepository = FakeWeatherRepository()
//        val getWeatherData = WeatherRepository(fakeWeatherRepository)
//        viewModel = WeatherViewModel(getWeatherData)
//    }
//
////    @Before
////    fun setUp() {
////        val fakeWeatherRepository = FakeWeatherRepository()
////        val getWeatherData = WeatherRepository(fakeWeatherRepository)
////        viewModel = WeatherViewModel(getWeatherData,d)
////
////    }
//
//    @Test
//    fun testGetWeatherData() = runBlockingTest {
//        // Test data
//        val latitude = 37.7749
//        val longitude = -122.4194
//
//        // Attach an observer to the weatherData LiveData
//        val observer: Observer<Resource<WeatherResponse>> = mock()
//        viewModel.weatherData.observeForever(observer)
//
//        // Call the function to be tested
//        viewModel.fetchWeatherData(latitude, longitude)
//
//        // Verify the repository method was called with the correct parameters
//        val expectedAppId = "YOUR_APP_ID" // Set your expected app ID
//        verify(viewModel.repository).getWeatherData(expectedAppId, latitude, longitude)
//
//        // Verify that the LiveData emits the expected Resource.Success containing the mocked data
//        val expectedWeatherResponse = fakeWeatherRepository.weather[0]
//        val expectedResource = Resource.Success(expectedWeatherResponse)
//        verify(observer).onChanged(expectedResource)
//
//        // Verify any other necessary assertions
//        // For example, you can check if the weatherData LiveData has the expected value.
//        assertEquals(expectedResource, viewModel.weatherData.value)
//
//        // Remove the observer
//        viewModel.weatherData.removeObserver(observer)
//    }
//}