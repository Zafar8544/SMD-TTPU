package uz.ttpu.weatherpatterns.data.repository

import kotlinx.coroutines.CancellationException
import uz.ttpu.weatherpatterns.data.api.WeatherApi
import uz.ttpu.weatherpatterns.data.model.Weather

interface WeatherRepository {
    suspend fun getWeather(city: String): Result<Weather>
}

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(city: String): Result<Weather> {
        return try {
            val weather = api.fetchWeather(city)
            Result.success(weather)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
