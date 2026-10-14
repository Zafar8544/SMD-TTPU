package uz.ttpu.weatherpatterns.mvc.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository
import uz.ttpu.weatherpatterns.mvc.model.WeatherModel
import uz.ttpu.weatherpatterns.mvc.view.WeatherView

class WeatherController(
    private val model: WeatherModel,
    private val view: WeatherView,
    private val repository: WeatherRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private var fetchJob: Job? = null

    fun selectCity(city: String) {
        fetchJob?.cancel()

        // 1. Controller mutates Model for loading state
        model.isLoading = true
        model.selectedCity = city
        model.weather = null
        model.error = null

        // 2. Controller triggers View rendering
        view.render()

        fetchJob = coroutineScope.launch {
            val result = repository.getWeather(city)

            // 3. Controller updates Model with response
            model.isLoading = false
            result.fold(
                onSuccess = { weather ->
                    model.weather = weather
                    model.error = null
                },
                onFailure = { throwable ->
                    model.weather = null
                    model.error = throwable.message ?: "Unknown error"
                }
            )

            // 4. Controller requests View re-render
            view.render()
        }
    }

    fun onDestroy() {
        fetchJob?.cancel()
    }
}
