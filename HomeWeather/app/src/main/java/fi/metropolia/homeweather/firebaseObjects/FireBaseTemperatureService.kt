package fi.metropolia.homeweather.firebaseObjects

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import fi.metropolia.homeweather.dataclass.Temperature
import kotlinx.coroutines.tasks.await

object FireBaseTemperatureService {
    private const val TAG = "FireBaseTemperatureService"

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
            Log.e(TAG, "Error getting temperature details")

        }
        return data
    }


    suspend fun postTemperatureData(temperature: Temperature) {
        val db = FirebaseFirestore.getInstance()
        val dbTemperature: CollectionReference = db.collection("temperature")

        dbTemperature.add(temperature).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }


}