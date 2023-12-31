package fi.metropolia.homeweather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.homeweather.dataclass.Humidity
import fi.metropolia.homeweather.dataclass.Temperature
import fi.metropolia.homeweather.repository.AppRepository
import kotlinx.coroutines.launch

class StaticScreenViewModel: ViewModel() {
    private val _temperatureData = MutableLiveData<List<Temperature>>()
    val temperatureData: LiveData<List<Temperature>> = _temperatureData
    private val _humidityData = MutableLiveData<List<Humidity>>()
    val humidityData: LiveData<List<Humidity>> = _humidityData

    init {
        viewModelScope.launch {
            _temperatureData.value = AppRepository.getFirebaseData("temperature", Temperature::class.java)
            Log.d("staticScreen", _temperatureData.value.toString())
            _humidityData.value = AppRepository.getFirebaseData("humidity", Humidity::class.java)
            Log.d("staticScreen", _humidityData.value.toString())
        }
    }

    fun getTemperatureData() {
        viewModelScope.launch {
            _temperatureData.value = AppRepository.getFirebaseData("temperature", Temperature::class.java)
        }

    }

    fun getHumidityData() {
        viewModelScope.launch {
            _humidityData.value = AppRepository.getFirebaseData("humidity", Humidity::class.java)
        }

    }
}