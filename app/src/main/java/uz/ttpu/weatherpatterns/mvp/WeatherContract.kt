package uz.ttpu.weatherpatterns.mvp

import uz.ttpu.weatherpatterns.data.model.Weather

interface WeatherContract {
    interface View {
        fun showLoading(city: String)
        fun showWeather(weather: Weather)
        fun showError(message: String)
    }

    interface Presenter {
        fun selectCity(city: String)
        fun detachView()
        fun onDestroy()
    }
}
