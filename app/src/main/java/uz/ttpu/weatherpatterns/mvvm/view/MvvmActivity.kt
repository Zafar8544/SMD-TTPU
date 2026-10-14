package uz.ttpu.weatherpatterns.mvvm.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.mvvm.viewmodel.WeatherViewModel
import uz.ttpu.weatherpatterns.mvvm.viewmodel.toStatusText
import uz.ttpu.weatherpatterns.ui.WeatherScreen
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

class MvvmActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels {
        WeatherModule.weatherViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherPatternsTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                WeatherScreen(
                    title = "MVVM Architecture Pattern",
                    statusText = uiState.toStatusText(),
                    onCitySelected = { city ->
                        viewModel.selectCity(city)
                    }
                )
            }
        }
    }
}
