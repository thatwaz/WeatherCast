package com.thatwaz.weathercast


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.repository.WeatherRepository
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import okhttp3.ResponseBody
import okhttp3.internal.tls.OkHostnameVerifier.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

//@ExperimentalCoroutinesApi
//class WeatherViewModelTest {
//
//    // Executes each task synchronously using Architecture Components.
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    // Use a TestCoroutineDispatcher to perform asynchronous operations in ViewModel
//    private val testDispatcher = TestCoroutineDispatcher()
//
//    // Mock WeatherRepository
//    private val mockRepository = mock<WeatherRepository>()
//
//    // ViewModel to be tested
//    private lateinit var weatherViewModel: WeatherViewModel
//
//    @Before
//    fun setUp() {
//        weatherViewModel = WeatherViewModel(mockRepository)
//    }
//
//    // Add your test cases below...
//
//
//
//    @Test
//    fun `test fetchWeatherData() success`() = runBlocking {
//        val latitude = 37.7749
//        val longitude = -122.4194
//        val weatherResponse = createMockWeatherResponse()
//
//        // Stubbing the repository's getWeatherData() function to return success
//        whenever(mockRepository.getWeatherData(any(), any(), any())).thenReturn(Response.success(weatherResponse))
//
//        weatherViewModel.fetchWeatherData(latitude, longitude)
//
//        // Verify that the weatherData LiveData is updated with the correct value
//        val capturedWeatherData = slot<WeatherResponse>()
//        verify(mockRepository).getWeatherData(eq(ApiConfig.APP_ID), eq(latitude), eq(longitude))
//        verify(weatherViewModel.weatherData).value = capture(capturedWeatherData)
//        assertEquals(weatherResponse, capturedWeatherData.captured)
//    }
//
//    @Test
//    fun `test fetchWeatherData() failure`() = runBlocking {
//        val latitude = 37.7749
//        val longitude = -122.4194
//
//        // Stubbing the repository's getWeatherData() function to return failure
//        whenever(mockRepository.getWeatherData(any(), any(), any())).thenReturn(Response.error(404, ResponseBody.create(null, "Not Found")))
//
//        weatherViewModel.fetchWeatherData(latitude, longitude)
//
//        // Verify that the error is logged
//        verify(mockRepository).getWeatherData(eq(ApiConfig.APP_ID), eq(latitude), eq(longitude))
//        verify(mockRepository).logError(any())
//    }
//
//    private fun createMockWeatherResponse(): WeatherResponse {
//        // Implement your mock WeatherResponse object here for testing purposes
//        // For simplicity, you can use Gson().fromJson() with a JSON string representing your response
//        // Alternatively, you can use any mocking library to create a mock WeatherResponse object
//    }
//}
