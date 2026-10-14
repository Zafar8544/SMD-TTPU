package uz.ttpu.weatherpatterns.mvvm.viewmodel

import uz.ttpu.weatherpatterns.data.model.Weather

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data class Loading(val city: String) : WeatherUiState
    data class Success(val weather: Weather) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

fun WeatherUiState.toStatusText(): String {
    return when (this) {
        is WeatherUiState.Idle -> "Select a city to view forecast"
        is WeatherUiState.Loading -> "Loading weather for $city..."
        is WeatherUiState.Success -> "${weather.city}: ${weather.temperatureC}°C, ${weather.condition}"
        is WeatherUiState.Error -> "Error: $message"
    }
}
