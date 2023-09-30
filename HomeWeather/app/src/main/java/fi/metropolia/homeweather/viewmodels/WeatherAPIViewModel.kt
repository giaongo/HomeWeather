package fi.metropolia.homeweather.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    private fun setTemp(value: Double) {
        _measureTemp.value = value
    }
    fun getWeatherData(lat: Double, long: Double){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = weatherAPIRepository.getWeatherData(lat, long)
                withContext(Dispatchers.Main) {
                    // Update the LiveData on the main thread
                    setTemp(response.main.temp)
                }
            } catch (se: SecurityException) {
                Log.e("WEATHER", "Failed to fetch weather Data.. $se")
            }
        }
    }
}