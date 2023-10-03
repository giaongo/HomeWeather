package fi.metropolia.homeweather.dataclass


import java.time.LocalDateTime


data class Temperature(
    var tempData: Float = 0.0F,
    var date: String = LocalDateTime.now().toString(),
)