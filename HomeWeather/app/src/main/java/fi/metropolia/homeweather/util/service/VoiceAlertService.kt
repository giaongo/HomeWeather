package fi.metropolia.homeweather.util.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import fi.metropolia.homeweather.dataclass.VoiceAlert
import fi.metropolia.homeweather.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale

class VoiceAlertService(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var enableAlert:Boolean = true

    init {
       textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.ENGLISH
            } else {
                Log.d("VoiceAlertService", "Error initializing text to speech engine.")
            }
        }
    }

    private fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        CoroutineScope(Dispatchers.IO).launch {
            AppRepository.postFirebaseData("alert", VoiceAlert(text, LocalDateTime.now().toString()))
        }
    }

    fun shutdown() {
        if (isSpeaking()) {
            textToSpeech?.stop()
        }
        textToSpeech?.shutdown()
    }

    private fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }

    fun raiseAlertForIndoor(temperature: Float? = null, humidity: Float? = null) {
        if (temperature == null || humidity == null) {
            return
        }

        enableAlert = if (temperature < 18 && enableAlert) {
            speak("Temperature is too low!")
            false
        } else if (temperature > 28 && enableAlert) {
            speak("Temperature is too high!")
            false
        } else {
            true
        }

        enableAlert = if (humidity < 30 && enableAlert) {
            speak("Humidity is too low!")
            false
        } else if (humidity > 60 && enableAlert) {
            speak("Humidity is too high!")
            false
        } else {
            true
        }
    }
}

