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
                val myData: T = document.toObject(dataClass)
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
    fun postFirebaseData(collectionName: String, dataToPost: Any) {
        val db = FirebaseFirestore.getInstance()
        val dbCollection: CollectionReference = db.collection(collectionName)

        dbCollection.add(dataToPost).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document to $collectionName: $e")
            }
    }

    /**
     * Fetch the documentId of a document according to fieldData of the document
     */
    suspend fun getFireBaseDocumentId(fieldName: String, fieldData: Any, collectionName: String): String {
        val db = FirebaseFirestore.getInstance()

        try {
            val querySnapshot = db.collection(collectionName)
                .whereEqualTo(fieldName, fieldData)
                .get()
                .await() // Wait for the query to complete asynchronously

            return if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                val documentId = document.id
                Log.d("alertScreen", "documentId in repo: $documentId")
                documentId
            } else {
                Log.d("alertScreen", "Document not found")
                ""
            }
        } catch (e: Exception) {
            // Handle any errors that occur during the query
            Log.e("alertScreen", "Error getting documents: $e")
            return ""
        }
    }

    /**
     * Update the fieldData according to document Id
     */
    fun updateDocument(fieldName: String, documentId: String, collectionName: String, fieldData: Any) {
        val db = FirebaseFirestore.getInstance()
        val dbCollection: CollectionReference = db.collection(collectionName)
        dbCollection.document(documentId).update(fieldName,fieldData).addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot updated with ID: $documentId")
        }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document to $collectionName: $e")
            }
    }
}
