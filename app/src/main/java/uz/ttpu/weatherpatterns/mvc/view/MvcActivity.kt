package uz.ttpu.weatherpatterns.mvc.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.mvc.controller.WeatherController
import uz.ttpu.weatherpatterns.mvc.model.WeatherModel
import uz.ttpu.weatherpatterns.ui.WeatherScreen
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

class MvcActivity : ComponentActivity(), WeatherView {

    private val model = WeatherModel()
    private lateinit var controller: WeatherController

    // Compose state holding the current rendered status text queried from the Model
    private val statusTextState = mutableStateOf(model.toStatusText())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        controller = WeatherController(
            model = model,
            view = this,
            repository = WeatherModule.repository
        )

        setContent {
            WeatherPatternsTheme {
                WeatherScreen(
                    title = "MVC Architecture Pattern",
                    statusText = statusTextState.value,
                    onCitySelected = { city ->
                        controller.selectCity(city)
                    }
                )
            }
        }
    }

    override fun render() {
        // Direct Model inspection in traditional MVC:
        // View queries state directly from the Model instance
        statusTextState.value = model.toStatusText()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }
}
