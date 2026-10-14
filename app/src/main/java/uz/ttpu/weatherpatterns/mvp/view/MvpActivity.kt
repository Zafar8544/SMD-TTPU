package uz.ttpu.weatherpatterns.mvp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import uz.ttpu.weatherpatterns.data.model.Weather
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.mvp.WeatherContract
import uz.ttpu.weatherpatterns.mvp.presenter.WeatherPresenter
import uz.ttpu.weatherpatterns.ui.WeatherScreen
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

class MvpActivity : ComponentActivity(), WeatherContract.View {

    private lateinit var presenter: WeatherContract.Presenter
    private val statusTextState = mutableStateOf("Select a city to view forecast")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        presenter = WeatherPresenter(
            view = this,
            repository = WeatherModule.repository
        )

        setContent {
            WeatherPatternsTheme {
                WeatherScreen(
                    title = "MVP Architecture Pattern",
                    statusText = statusTextState.value,
                    onCitySelected = { city ->
                        presenter.selectCity(city)
                    }
                )
            }
        }
    }

    override fun showLoading(city: String) {
        statusTextState.value = "Loading weather for $city..."
    }

    override fun showWeather(weather: Weather) {
        statusTextState.value = "${weather.city}: ${weather.temperatureC}°C, ${weather.condition}"
    }

    override fun showError(message: String) {
        statusTextState.value = "Error: $message"
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.onDestroy()
    }
}
