package fi.metropolia.homeweather.util.service

import retrofit2.http.GET
import retrofit2.http.Query
data class WeatherAPIDataResponse(
    val query: WeatherAPITemp
)
data class WeatherAPITemp (
    val temp: Double
)
interface WeatherAPIDataService {
    @GET("weather")
    suspend fun getWeatherAPIData(
        @Query("lat") lat: Double,
        @Query("long") long: Double,
        @Query("API_KEY") apiKey: String,
    ) : WeatherAPIDataResponse
}