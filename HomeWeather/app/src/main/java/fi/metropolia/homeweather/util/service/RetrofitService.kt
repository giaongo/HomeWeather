package fi.metropolia.homeweather.util.service

import androidx.lifecycle.MutableLiveData
import retrofit2.http.GET
import retrofit2.http.Query
data class WeatherAPIDataResponse(
    val main: WeatherAPITemp
)
data class WeatherAPITemp (
    val temp: Double
)
interface WeatherAPIDataService {
    @GET("weather")
    suspend fun getWeatherAPIData(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ) : WeatherAPIDataResponse
}