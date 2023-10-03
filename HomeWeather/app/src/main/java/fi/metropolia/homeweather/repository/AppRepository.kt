package fi.metropolia.homeweather.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import fi.metropolia.homeweather.dataclass.Humidity
import fi.metropolia.homeweather.dataclass.Temperature
import kotlinx.coroutines.tasks.await

object AppRepository {
    private const val TEMP_TAG = "FireBaseTemperatureService"
    private const val HUMIDITY_TAG = "FireBaseHumidityService"
    private const val TAG = "FirebaseDataHandler"

    /**
     * Get all documents for 1 collection from Firebase
     */
    suspend fun <T> getFirebaseData(collectionName: String, dataClass: Class<T>): List<T> {
        val db = FirebaseFirestore.getInstance()
        val data = mutableListOf<T>()
        try {
            val querySnapshot = db.collection(collectionName).get().await()
            for (document in querySnapshot) {
                val myData:T = document.toObject(dataClass)
                data.add(myData)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting $collectionName")
        }
        return data
    }

    /**
     * post document to Firebase
     */
    suspend fun postFirebaseData(collectionName: String, dataToPost:Any) {
        val db = FirebaseFirestore.getInstance()
        val dbCollection:CollectionReference = db.collection(collectionName)

        dbCollection.add(dataToPost).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.e(TAG,"Error adding document to $collectionName")
        }
    }


    /**
     * --------------------------------------
     * TODO: Note for Anish
     * Do you think if it is better to remove the below firebase functions for humidity and temperature.
     * And instead utilizes the above generic functions for all cases. I have used the above generic ones
     * for the alert and they seem to work fine.
     * --------------------------------------
     */

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
