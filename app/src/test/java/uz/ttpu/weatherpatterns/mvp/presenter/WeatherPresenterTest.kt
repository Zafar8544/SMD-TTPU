package uz.ttpu.weatherpatterns.mvp.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.model.Weather
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl
import uz.ttpu.weatherpatterns.mvp.WeatherContract

class WeatherPresenterTest {

    private lateinit var presenter: WeatherPresenter
    private lateinit var fakeView: TestWeatherView

    class TestWeatherView : WeatherContract.View {
        val events = mutableListOf<String>()
        var lastWeather: Weather? = null
        var lastError: String? = null

        override fun showLoading(city: String) {
            events.add("loading:$city")
        }

        override fun showWeather(weather: Weather) {
            events.add("weather:${weather.city}")
            lastWeather = weather
        }

        override fun showError(message: String) {
            events.add("error:$message")
            lastError = message
        }
    }

    @Before
    fun setUp() {
        fakeView = TestWeatherView()
        val fakeApi = FakeWeatherApi(latencyMs = 0L)
        val repository = WeatherRepositoryImpl(fakeApi)

        @Suppress("DEPRECATION")
        val testScope = CoroutineScope(Dispatchers.Unconfined)

        presenter = WeatherPresenter(
            view = fakeView,
            repository = repository,
            coroutineScope = testScope
        )
    }

    @Test
    fun selectCity_knownCity_showsLoadingThenWeather() {
        presenter.selectCity("Tashkent")

        assertEquals(2, fakeView.events.size)
        assertEquals("loading:Tashkent", fakeView.events[0])
        assertEquals("weather:Tashkent", fakeView.events[1])
        assertEquals("Tashkent", fakeView.lastWeather?.city)
        assertEquals(24, fakeView.lastWeather?.temperatureC)
    }

    @Test
    fun selectCity_unknownCity_showsLoadingThenError() {
        presenter.selectCity("Atlantis")

        assertEquals(2, fakeView.events.size)
        assertEquals("loading:Atlantis", fakeView.events[0])
        assertEquals("error:No forecast for Atlantis", fakeView.events[1])
        assertEquals("No forecast for Atlantis", fakeView.lastError)
    }

    @Test
    fun selectCity_sequentialRequests_completesLatestRequest() {
        presenter.selectCity("Tashkent")
        presenter.selectCity("Samarkand")

        assertTrue(fakeView.events.contains("weather:Samarkand"))
        assertEquals("Samarkand", fakeView.lastWeather?.city)
        assertEquals(21, fakeView.lastWeather?.temperatureC)
    }

    @Test
    fun detachView_cancelsJobAndPreventsCallbacks() {
        val fakeApi = FakeWeatherApi(latencyMs = 1000L)
        val repository = WeatherRepositoryImpl(fakeApi)
        @Suppress("DEPRECATION")
        val testScope = CoroutineScope(Dispatchers.Unconfined)
        val presenterWithDelay = WeatherPresenter(fakeView, repository, testScope)

        presenterWithDelay.selectCity("Tashkent")
        presenterWithDelay.detachView()

        // Verify only initial loading event was recorded, no weather/error after detachment
        assertEquals(1, fakeView.events.size)
        assertEquals("loading:Tashkent", fakeView.events[0])
    }
}
