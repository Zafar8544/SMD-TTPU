package uz.ttpu.weatherpatterns.mvi.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl
import uz.ttpu.weatherpatterns.mvi.model.WeatherIntent
import uz.ttpu.weatherpatterns.mvi.model.toStatusText

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherMviViewModelTest {

    private lateinit var viewModel: WeatherMviViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val fakeApi = FakeWeatherApi(latencyMs = 0L)
        val repository = WeatherRepositoryImpl(fakeApi)
        viewModel = WeatherMviViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectCity_knownCity_endsContentStateWithWeatherAndNoLoading() {
        viewModel.onIntent(WeatherIntent.SelectCity("Tashkent"))

        val state = viewModel.state.value
        assertFalse("Expected isLoading to be false", state.isLoading)
        assertEquals("Tashkent", state.weather?.city)
        assertEquals(24, state.weather?.temperatureC)
        assertNull("Expected error to be null", state.error)
    }

    @Test
    fun selectCity_unknownCity_endsWithErrorState() {
        viewModel.onIntent(WeatherIntent.SelectCity("Atlantis"))

        val state = viewModel.state.value
        assertFalse("Expected isLoading to be false", state.isLoading)
        assertNull("Expected weather to be null", state.weather)
        assertEquals("No forecast for Atlantis", state.error)
    }

    @Test
    fun dismissError_clearsErrorAndPreservesPreviousWeather() {
        // 1. Fetch valid weather for Tashkent
        viewModel.onIntent(WeatherIntent.SelectCity("Tashkent"))
        assertEquals("Tashkent", viewModel.state.value.weather?.city)

        // 2. Trigger invalid request for Atlantis causing error
        viewModel.onIntent(WeatherIntent.SelectCity("Atlantis"))
        assertEquals("No forecast for Atlantis", viewModel.state.value.error)

        // 3. Send DismissError intent
        viewModel.onIntent(WeatherIntent.DismissError)

        val stateAfterDismiss = viewModel.state.value
        assertNull("Expected error to be cleared", stateAfterDismiss.error)
        assertEquals("Tashkent", stateAfterDismiss.weather?.city)
        assertEquals("Tashkent: 24°C, Sunny", stateAfterDismiss.toStatusText())
    }
}
