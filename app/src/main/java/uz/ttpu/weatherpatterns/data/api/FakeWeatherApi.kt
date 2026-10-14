package uz.ttpu.weatherpatterns.data.api

import kotlinx.coroutines.delay
import uz.ttpu.weatherpatterns.data.model.Weather

class FakeWeatherApi(
    private val latencyMs: Long = 1500L
) : WeatherApi {

    override suspend fun fetchWeather(city: String): Weather {
        if (latencyMs > 0) {
            delay(latencyMs)
        }
        return when (city.trim()) {
            "Tashkent" -> Weather(
                city = "Tashkent",
                temperatureC = 24,
                condition = "Sunny"
            )
            "Samarkand" -> Weather(
                city = "Samarkand",
                temperatureC = 21,
                condition = "Cloudy"
            )
            "Bukhara" -> Weather(
                city = "Bukhara",
                temperatureC = 27,
                condition = "Windy"
            )
            else -> throw IllegalArgumentException("No forecast for $city")
        }
    }
}
