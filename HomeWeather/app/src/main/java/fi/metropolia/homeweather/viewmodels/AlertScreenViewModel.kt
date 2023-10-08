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
            _alertData.value = AppRepository.getFirebaseData("alert", VoiceAlert::class.java)
        }
    }

    /**
     * Method to refresh the data when info is added by user
     */
    suspend fun refresh () {
        _alertData.value = AppRepository.getFirebaseData("alert", VoiceAlert::class.java)
    }

    /**
     * Update data to the firebase
     */
    suspend fun updateFireBaseData (fieldName: String, documentId: String, collectionName: String, fieldData: Any) {
        AppRepository.updateDocument(fieldName, documentId,collectionName, fieldData)
    }

    /**
     * Method to get the documentId according to field data
     */
    suspend fun getDocumentId(fieldName: String, fieldData: Any, collectionName: String):String {
        return AppRepository.getFireBaseDocumentId(fieldName, fieldData = fieldData, collectionName = collectionName)
    }

}