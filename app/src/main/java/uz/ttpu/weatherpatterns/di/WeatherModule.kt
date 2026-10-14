package uz.ttpu.weatherpatterns.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl
import uz.ttpu.weatherpatterns.mvi.viewmodel.WeatherMviViewModel
import uz.ttpu.weatherpatterns.mvvm.viewmodel.WeatherViewModel

object WeatherModule {
    val repository: WeatherRepository by lazy {
        WeatherRepositoryImpl(
            api = FakeWeatherApi(latencyMs = 3_000L)
        )
    }

    val weatherViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                return WeatherViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    val weatherMviViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherMviViewModel::class.java)) {
                return WeatherMviViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
