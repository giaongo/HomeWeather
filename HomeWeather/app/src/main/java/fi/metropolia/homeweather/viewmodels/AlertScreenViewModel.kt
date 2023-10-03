package fi.metropolia.homeweather.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import fi.metropolia.homeweather.dataclass.VoiceAlert
import fi.metropolia.homeweather.repository.AppRepository


class AlertScreenViewModel: ViewModel() {
    private val _alertData = MutableLiveData<List<VoiceAlert>>()
    val alertData: LiveData<List<VoiceAlert>> = _alertData

    init {
        viewModelScope.launch {
            _alertData.value = AppRepository.getAlertData()
        }
    }

}