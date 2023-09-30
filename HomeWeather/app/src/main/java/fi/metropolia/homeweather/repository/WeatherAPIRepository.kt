package fi.metropolia.homeweather.repository

import fi.metropolia.homeweather.util.service.WeatherAPIDataResponse
import fi.metropolia.homeweather.util.service.WeatherAPIDataService
import io.github.cdimascio.dotenv.dotenv
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dotenv = dotenv {
    directory = "/assets"
    filename = "env" // instead of '.env', use 'env'
}
class WeatherAPIRepository {
    private val baseUrl = dotenv.get("BASE_URL")
    private val weatherAPIDataService : WeatherAPIDataService
    private val API_KEY = dotenv.get("API_KEY")
    private val units = dotenv.get("UNITS")
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherAPIDataService = retrofit.create(WeatherAPIDataService::class.java)
    }

    suspend fun getWeatherData(lat: Double, long: Double): WeatherAPIDataResponse {
        return weatherAPIDataService.getWeatherAPIData(lat = lat, long = long, apiKey = API_KEY, units=units)
    }
}