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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import fi.metropolia.homeweather.util.service.SensorMeasurement
import fi.metropolia.homeweather.viewmodels.WeatherAPIViewModel
import getUserLocation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timerTask

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               temperature: SensorMeasurement?,
               humidity: SensorMeasurement?, ) {
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    var context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val temperatureData = String.format("%.2f", temperature?.value ?: 0.0f)
    val humidityData = String.format("%.2f", humidity?.value ?: 0.0f)
    val temperatureTimeStamp = temperature?.timeStamp?.format(formatter) ?: "No Time"
    val humidityTimeStamp = humidity?.timeStamp?.format(formatter) ?: "No Time"

    // This is being used to upload temperature data to the firebase atm.
    /*val db = FirebaseFirestore.getInstance()
    val temp = temperature?.value?.let { Temperature(it, temperature.timeStamp.toString()) }
    if (temp != null) {
        db.collection("temperature").add(temp)
    }*/

    //This is being used to upload humidity data to the firebase atm
   /* val db = FirebaseFirestore.getInstance()
    val humidityFirebaseData = humidity?.value?.let { Humidity(it, humidity.timeStamp.toString()) }
    if (humidityFirebaseData != null) {
        db.collection("humidity").add(humidityFirebaseData)
    }*/
    // This is the way that will be implemented in future
   /* val humidityFirebaseData = humidity?.value?.let { Humidity(it, humidity.timeStamp.toString()) }
    GlobalScope.launch {
        if (humidityFirebaseData != null) {
            FireBaseTemperatureService.postHumidityData(humidityFirebaseData)
        }
    }*/

    var weatherApiViewModel : WeatherAPIViewModel = viewModel()

    var userLocation = getUserLocation(context = context).value

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
            Spacer(Modifier.height(30.dp))
            Row (modifier = modifier
                .fillMaxWidth()
            ) {
                displayWeatherInfo(
                    fraction = 0.5F,
                    measureLocation = "Currently Inside",
                    measureTemp = "$temperatureData°C"
                )
                displayWeatherInfo(
                    measureLocation = "Currently Outside",
                    measureTemp = "${measureTemp.value}°C"
                )
            }
        } else {
            Spacer(Modifier.height(30.dp))
            Row (modifier = modifier
                .fillMaxWidth()
            ) {
                displayWeatherInfo(
                    fraction = 0.5F,
                    measureLocation = "Currently Inside",
                    measureTemp = "20%"
                )
                displayWeatherInfo(
                    measureLocation = "Currently Outside",
                    measureTemp = "${measureHumidity.value}%"
                )
            }
        }
        CircleInfo()
    }
}

@Composable
fun displayWeatherInfo(fraction: Float = 1F, measureLocation: String, measureTemp: String) {
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
            text = "$measureLocation",
            color = Color.Gray
        )
        Text(
            text = "$measureTemp",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CircleInfo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val brush = Brush.horizontalGradient(listOf(Color.Red, Color.Blue))
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            onDraw = {
                drawCircle(brush)
            }
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .padding(16.dp),
            onDraw = {
                drawCircle(Color.Blue)
            }
        )
        Text(text = "10*C", color = Color.Green, fontSize = 20.sp)
    }


}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        temperature = SensorMeasurement(50.0f, LocalDateTime.now()),
        humidity = SensorMeasurement(100.0f, LocalDateTime.now())
    )
}