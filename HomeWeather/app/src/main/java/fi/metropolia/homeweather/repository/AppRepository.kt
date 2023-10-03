package fi.metropolia.homeweather.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import fi.metropolia.homeweather.dataclass.Humidity
import fi.metropolia.homeweather.dataclass.Temperature
import fi.metropolia.homeweather.dataclass.VoiceAlert
import kotlinx.coroutines.tasks.await

object AppRepository {
    private const val TEMP_TAG = "FireBaseTemperatureService"
    private const val HUMIDITY_TAG = "FireBaseHumidityService"

    /**
     * Post alert data to firebase
     */
    suspend fun postAlertData(alert: VoiceAlert) {
        val db = FirebaseFirestore.getInstance()
        val dbAlert = db.collection("alert")

        dbAlert.add(alert).addOnSuccessListener { documentReference ->
            println("DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding document $e")
        }
    }

    /**
     * Read alert data from firebase
     */
    suspend fun getAlertData(): List<VoiceAlert> {
        val db = FirebaseFirestore.getInstance()
        val data = mutableListOf<VoiceAlert>()
        try {
            val querySnapshot = db.collection("alert").get().await()
            for (document in querySnapshot) {
                Log.d("Alert", "${document.id} => ${document.data}")
                val myData: VoiceAlert = document.toObject(VoiceAlert::class.java)
                data.add(myData)
            }
            Log.d("AlertScreenViewModel", "Alert data: $data")

        } catch (e: Exception) {
            Log.e("AlertScreenViewModel", "Error getting alert details")
        }
        return data
    }


    /**
     * Get humidity data from firebase
     */
    suspend fun getHumidityData(): List<Humidity> {
        val db = FirebaseFirestore.getInstance()
        val data = mutableListOf<Humidity>()
        try {
            Log.d("Checkpoint", "checkpoint 1")
            val querySnapshot = db.collection("humidity").get().await()
            for (document in querySnapshot) {
                val myData = document.toObject(Humidity::class.java)
                data.add(myData)
            }

        } catch (e: Exception) {
            Log.e(HUMIDITY_TAG, "Error getting temperature details")

        }
        return data
    }

    /**
     * Post humidity data to firebase
     */
    suspend fun postHumidityData(humidity: Humidity) {
        val db = FirebaseFirestore.getInstance()
        val dbTemperature: CollectionReference = db.collection("humidity")

        dbTemperature.add(humidity).addOnSuccessListener { documentReference ->
            Log.d(TEMP_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
            .addOnFailureListener { e ->
                Log.w(TEMP_TAG, "Error adding document", e)
            }

    }

    /**
     * Get temperature data from firebase
     */
    suspend fun getTemperatureData(): List<Temperature> {
        val db = FirebaseFirestore.getInstance()
        val data = mutableListOf<Temperature>()
        try {
            Log.d("Checkpoint", "checkpoint 1")
            val querySnapshot = db.collection("temperature").get().await()
            for (document in querySnapshot) {
                val myData = document.toObject(Temperature::class.java)
                data.add(myData)
            }

        } catch (e: Exception) {
            Log.e(TEMP_TAG, "Error getting temperature details")

        }
        return data
    }


    /**
     * Post temperature data to firebase
     */
    suspend fun postTemperatureData(temperature: Temperature) {
        val db = FirebaseFirestore.getInstance()
        val dbTemperature: CollectionReference = db.collection("temperature")

        dbTemperature.add(temperature).addOnSuccessListener { documentReference ->
            Log.d(HUMIDITY_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
            .addOnFailureListener { e ->
                Log.w(HUMIDITY_TAG, "Error adding document", e)
            }

    }




}
