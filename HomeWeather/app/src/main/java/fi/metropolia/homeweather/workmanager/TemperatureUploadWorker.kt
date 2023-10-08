package fi.metropolia.homeweather.workmanager

import android.content.Context
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import fi.metropolia.homeweather.dataclass.Temperature
import fi.metropolia.homeweather.repository.AppRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TemperatureUploadWorker(
    appContext: Context,
    private val params: WorkerParameters
): Worker(appContext, params) {
    @RequiresApi(34)
    override fun doWork(): Result {
        val serializedData = inputData.getString(TEMP_DATA)
        Log.d("workManager", "serialized data from worker: $serializedData")
            return try {
                GlobalScope.launch {
                    if(serializedData != null) {
                        val gson = Gson()
                        val temp = gson.fromJson(serializedData, Temperature::class.java)
                         if(temp != null) {
                            AppRepository.postFirebaseData("temperature", temp)
                            Log.d("workManager", "data uploaded")
                         }
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