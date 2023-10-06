package fi.metropolia.homeweather.workmanager

import android.content.Context
import android.health.connect.datatypes.units.Temperature
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import fi.metropolia.homeweather.repository.AppRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataUploadWorker(
    appContext: Context,
    private val params: WorkerParameters
): Worker(appContext, params) {
    @RequiresApi(34)
    override fun doWork(): Result {
        val serializedData = inputData.getString("temp_data")
        Log.d("workManager", "serialized data from worker: $serializedData")
            return try {
                GlobalScope.launch {
                    if(serializedData != null) {
                        val gson = Gson()
                        val temp = gson.fromJson(serializedData, Temperature::class.java)
                        if(temp.inCelsius != 0.0) {
                            AppRepository.postFirebaseData("temperature", temp)
                        }
                        Log.d("workManager", "data uploaded")
                    }
                }
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }

companion object{
    const val TEMP_DATA = "temp_data"
}
}