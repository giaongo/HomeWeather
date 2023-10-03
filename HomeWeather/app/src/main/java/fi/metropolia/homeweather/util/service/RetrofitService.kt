package fi.metropolia.homeweather.util.service

import retrofit2.http.GET
import retrofit2.http.Query
data class WeatherAPIDataResponse(
    val main: WeatherAPITempAndHumidity
)
data class WeatherAPITempAndHumidity (
    val temp: Double,
    val humidity: Int
)
interface WeatherAPIDataService {
    @GET("weather")
    suspend fun getWeatherAPIData(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
    ) : WeatherAPIDataResponse
}