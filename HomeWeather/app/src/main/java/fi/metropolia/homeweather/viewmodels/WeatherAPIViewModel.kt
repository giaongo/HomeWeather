package fi.metropolia.homeweather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.homeweather.repository.WeatherAPIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherAPIViewModel() : ViewModel() {
    private val weatherAPIRepository = WeatherAPIRepository()
    fun getWeatherData(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = weatherAPIRepository.getWeatherData(lat, long)
            response.query.temp
        }
    }
}