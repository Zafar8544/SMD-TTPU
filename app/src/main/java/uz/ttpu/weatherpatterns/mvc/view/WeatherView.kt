package uz.ttpu.weatherpatterns.mvc.view

interface WeatherView {
    /**
     * In traditional MVC, render() has no parameters because the View holds a direct
     * reference to the Model and queries the Model's state directly when rendering.
     */
    fun render()
}
