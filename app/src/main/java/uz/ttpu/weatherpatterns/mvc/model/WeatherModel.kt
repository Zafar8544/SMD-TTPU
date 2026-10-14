package uz.ttpu.weatherpatterns.mvc.model

import uz.ttpu.weatherpatterns.data.model.Weather

class WeatherModel {
    var isLoading: Boolean = false
    var selectedCity: String? = null
    var weather: Weather? = null
    var error: String? = null

    fun toStatusText(): String {
        return when {
            isLoading -> "Loading weather for ${selectedCity ?: "city"}..."
            error != null -> "Error: $error"
            weather != null -> "${weather!!.city}: ${weather!!.temperatureC}°C, ${weather!!.condition}"
            else -> "Select a city to view forecast"
        }
    }
}
