package fi.metropolia.homeweather.firebaseObjects

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import fi.metropolia.homeweather.dataclass.Humidity
import kotlinx.coroutines.tasks.await

object FireBaseHumidityService {
    private const val TAG = "FireBaseHumidityService"

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
            Log.e(TAG, "Error getting temperature details")

        }
        return data
    }

    suspend fun postHumidityData(humidity: Humidity) {
        val db = FirebaseFirestore.getInstance()
        val dbTemperature: CollectionReference = db.collection("humidity")

        dbTemperature.add(humidity).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }
}