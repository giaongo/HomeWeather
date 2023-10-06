package fi.metropolia.homeweather.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AppRepository {
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

}
