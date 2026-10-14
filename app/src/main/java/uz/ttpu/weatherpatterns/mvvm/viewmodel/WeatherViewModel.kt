package uz.ttpu.weatherpatterns.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun selectCity(city: String) {
        fetchJob?.cancel()

        _uiState.value = WeatherUiState.Loading(city)

        fetchJob = viewModelScope.launch {
            val result = repository.getWeather(city)
            result.fold(
                onSuccess = { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                },
                onFailure = { throwable ->
                    _uiState.value = WeatherUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }
}
