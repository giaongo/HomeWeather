package fi.metropolia.homeweather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.homeweather.repository.WeatherAPIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherAPIViewModel() : ViewModel() {
    private val weatherAPIRepository = WeatherAPIRepository()
    private val _measureTemp = MutableLiveData<Double>()
    val measureTemp: LiveData<Double> get() = _measureTemp

    private val _measureHumidity = MutableLiveData<Int>()
    val measureHumidity: LiveData<Int> get() = _measureHumidity

    private fun setTemp(value: Double) {
        _measureTemp.value = value
    }
    private fun setHumidity(value: Int) {
        _measureHumidity.value = value
    }

    fun getWeatherData(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = weatherAPIRepository.getWeatherData(lat, long)
                withContext(Dispatchers.Main) {
                    // Update the LiveData on the main thread
                    setTemp(response.main.temp)
                    setHumidity(response.main.humidity)
                }
            } catch (se: SecurityException) {
                Log.e("WEATHER", "Failed to fetch weather Data.. $se")
            }
        }
    }
}