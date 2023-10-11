package fi.metropolia.homeweather.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.metropolia.homeweather.util.service.BluetoothLEService

class AppViewModel : ViewModel() {
    private val _bluetoothLEServiceLiveData = MutableLiveData<BluetoothLEService?>()
    val bluetoothLEServiceLiveData: LiveData<BluetoothLEService?> = _bluetoothLEServiceLiveData

    fun updateServiceLiveData(newData:BluetoothLEService?) {
        _bluetoothLEServiceLiveData.value = newData
    }

    override fun onCleared() {
        super.onCleared()
        _bluetoothLEServiceLiveData.value = null
    }
}