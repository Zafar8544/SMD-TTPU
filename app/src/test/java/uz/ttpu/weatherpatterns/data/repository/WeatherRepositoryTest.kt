package uz.ttpu.weatherpatterns.data.repository

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi

class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        // Use latencyMs = 0 for instant deterministic unit tests
        val fakeApi = FakeWeatherApi(latencyMs = 0L)
        repository = WeatherRepositoryImpl(fakeApi)
    }

    @Test
    fun getWeather_forTashkent_returnsSuccessWithTemperature24() = runBlocking {
        val result = repository.getWeather("Tashkent")

        assertTrue("Expected result to be success", result.isSuccess)
        val weather = result.getOrNull()
        assertEquals("Tashkent", weather?.city)
        assertEquals(24, weather?.temperatureC)
        assertEquals("Sunny", weather?.condition)
    }

    @Test
    fun getWeather_forAtlantis_returnsFailure() = runBlocking {
        val result = repository.getWeather("Atlantis")

        assertTrue("Expected result to be failure", result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue("Exception should be IllegalArgumentException", exception is IllegalArgumentException)
        assertEquals("No forecast for Atlantis", exception?.message)
    }
}
