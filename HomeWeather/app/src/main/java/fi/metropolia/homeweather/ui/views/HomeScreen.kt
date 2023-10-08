package fi.metropolia.homeweather.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.dataclass.Humidity
import fi.metropolia.homeweather.dataclass.Temperature
import fi.metropolia.homeweather.viewmodels.WeatherAPIViewModel
import getUserLocation
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.timerTask


@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               temperature: Temperature?,
               humidity: Humidity?) {
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    val context = LocalContext.current
    val temperatureData = String.format("%.2f", temperature?.tempData ?: 0.0f)
    val humidityData = String.format("%.2f", humidity?.humidityData ?: 0.0f)


    val weatherApiViewModel = viewModel<WeatherAPIViewModel>()

    val userLocation = getUserLocation(context = context).value

    val measureTemp = weatherApiViewModel.measureTemp.observeAsState()
    val measureHumidity = weatherApiViewModel.measureHumidity.observeAsState()

    Timer().scheduleAtFixedRate(timerTask {
        weatherApiViewModel.getWeatherData(userLocation.latitude, userLocation.longitude)
    }, 0, 3600000L) //3600 seconds in milliseconds


    val titles = listOf("Temperature", "Humidity")
    Column(modifier = modifier) {
        TabRow(selectedTabIndex = tabIndex) {
            titles.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    icon = {
                        when (index) {
                            0 -> Icon(
                                painter = painterResource(id = R.drawable.thermostat),
                                contentDescription = "temperature tab"
                            )

                            1 -> Icon(
                                painter = painterResource(id = R.drawable.humidity),
                                contentDescription = "Humidity tab"
                            )
                        }
                    }
                )
            }
        }
        if (tabIndex == 0) {
            DisplayTabContent(
                modifier = modifier,
                sensorData = "$temperatureData°C",
                weatherData = "${measureTemp.value.toString()}°C"
            )
            CircleInfo("$temperatureData°C", defineTempDescription(temperature))
        } else {
            DisplayTabContent(
                modifier = modifier,
                sensorData = "$humidityData %",
                weatherData = "${measureHumidity.value.toString()} %"
            )
            CircleInfo("$humidityData %", defineHumidityDescription(humidity))
        }
    }
}

@Composable
fun DisplayTabContent(
    modifier: Modifier,
    sensorData: String,
    weatherData: String
) {
    Spacer(Modifier.height(30.dp))
    Row (modifier = modifier
        .fillMaxWidth()
    ) {
        DisplayWeatherInfo(
            fraction = 0.5F,
            measureLocation = "Currently Inside",
            measureTemp = sensorData
        )
        DisplayWeatherInfo(
            measureLocation = "Currently Outside",
            measureTemp = weatherData
        )
    }
}

@Composable
fun DisplayWeatherInfo(fraction: Float = 1F, measureLocation: String, measureTemp: String) {
    Column (modifier = Modifier
        .fillMaxWidth(fraction = fraction)
        .drawBehind {
            val strokeWidth = 2f
            val x = size.width - strokeWidth
            val y = size.height - strokeWidth
            drawLine(
                color = Color.LightGray,
                start = Offset(x, 0f),
                end = Offset(x, y),
                strokeWidth = strokeWidth
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = measureLocation,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            text = measureTemp,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CircleInfo(currentlyInsideWeatherInfo: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val innerCircle = Brush.linearGradient(
            colors = listOf((MaterialTheme.colorScheme.inversePrimary), MaterialTheme.colorScheme.background)
        )
        val outerCircle = Brush.linearGradient(
            colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.background)
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            onDraw = {
                drawCircle(outerCircle)
            }
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .padding(16.dp),
            onDraw = {
                drawCircle(innerCircle)
            }
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentlyInsideWeatherInfo,
                color = Color.Black,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = description,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

fun defineTempDescription(temperature: Temperature?): String {
    val temp = temperature?.tempData ?: 0.0f
    return if (temp > 0 && temp <= 15) {
        "Cool"
    } else if (temp > 15 && temp <= 30) {
        "Warm"
    } else if (temp > 30) {
        "Hot"
    } else {
        "Freezing"
    }
}

fun defineHumidityDescription(humidity: Humidity?) : String {
    val humid = humidity?.humidityData ?: 0.0f
    return if (humid <= 30) {
        "Dry"
    } else if (humid > 30 && humid <= 60) {
        "Standard"
    } else {
        "Moist"
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        temperature = Temperature(50.0f, LocalDateTime.now().toString()),
        humidity = Humidity(100.0f, LocalDateTime.now().toString())
    )
}