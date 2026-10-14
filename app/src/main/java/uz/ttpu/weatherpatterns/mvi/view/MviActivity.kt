package uz.ttpu.weatherpatterns.mvi.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.mvi.model.WeatherIntent
import uz.ttpu.weatherpatterns.mvi.model.toStatusText
import uz.ttpu.weatherpatterns.mvi.viewmodel.WeatherMviViewModel
import uz.ttpu.weatherpatterns.ui.WeatherScreen
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

class MviActivity : ComponentActivity() {

    private val viewModel: WeatherMviViewModel by viewModels {
        WeatherModule.weatherMviViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherPatternsTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()

                WeatherScreen(
                    title = "MVI Architecture Pattern",
                    statusText = state.toStatusText(),
                    onCitySelected = { city ->
                        viewModel.onIntent(WeatherIntent.SelectCity(city))
                    },
                    onDismissError = {
                        viewModel.onIntent(WeatherIntent.DismissError)
                    }
                )
            }
        }
    }
}
