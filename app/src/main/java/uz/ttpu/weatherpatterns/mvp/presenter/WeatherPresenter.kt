package uz.ttpu.weatherpatterns.mvp.presenter

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository
import uz.ttpu.weatherpatterns.mvp.WeatherContract

class WeatherPresenter(
    private var view: WeatherContract.View?,
    private val repository: WeatherRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : WeatherContract.Presenter {

    private var fetchJob: Job? = null

    override fun selectCity(city: String) {
        fetchJob?.cancel()

        Log.d("MVP", "selectCity: $city")
        view?.showLoading(city)

        fetchJob = coroutineScope.launch {
            val result = repository.getWeather(city)
            result.fold(
                onSuccess = { weather ->
                    if (view != null) {
                        Log.d("MVP", "Weather fetched successfully for ${weather.city}")
                        view?.showWeather(weather)
                    } else {
                        Log.w("MVP", "View detached before weather callback delivered")
                    }
                },
                onFailure = { throwable ->
                    if (view != null) {
                        Log.d("MVP", "Weather fetch failed: ${throwable.message}")
                        view?.showError(throwable.message ?: "Unknown error")
                    } else {
                        Log.w("MVP", "View detached before error callback delivered")
                    }
                }
            )
        }
    }

    override fun detachView() {
        Log.d("MVP", "detachView called: cancelling fetchJob and nullifying view reference")
        fetchJob?.cancel()
        view = null
    }

    override fun onDestroy() {
        detachView()
    }
}
