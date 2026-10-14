package uz.ttpu.weatherpatterns.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository
import uz.ttpu.weatherpatterns.mvi.model.WeatherIntent
import uz.ttpu.weatherpatterns.mvi.model.WeatherState

class WeatherMviViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private var fetchJob: Job? = null

    fun onIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.SelectCity -> handleSelectCity(intent.city)
            is WeatherIntent.DismissError -> handleDismissError()
        }
    }

    private fun handleSelectCity(city: String) {
        fetchJob?.cancel()

        _state.value = _state.value.copy(
            isLoading = true,
            selectedCity = city,
            error = null
        )

        fetchJob = viewModelScope.launch {
            val result = repository.getWeather(city)
            result.fold(
                onSuccess = { weather ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        weather = weather,
                        error = null
                    )
                },
                onFailure = { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    private fun handleDismissError() {
        _state.value = _state.value.copy(
            error = null
        )
    }
}
