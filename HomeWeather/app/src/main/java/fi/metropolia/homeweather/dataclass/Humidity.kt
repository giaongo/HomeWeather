package fi.metropolia.homeweather.dataclass

import java.time.LocalDateTime

data class Humidity(
    var humidityData: Float = 0.0F,
    var date: String = LocalDateTime.now().toString(),
)
