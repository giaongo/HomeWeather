package fi.metropolia.homeweather.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.util.service.SensorMeasurement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               temperature: SensorMeasurement?,
               humidity: SensorMeasurement?) {
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
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
                                contentDescription = "temperature tab"
                            )
                        }

                    }
                )
            }
        }

        Text(text = "Temperature is $temperatureData°C at $temperatureTimeStamp")
        Text(text = "Humidity is $humidityData% at $humidityTimeStamp")
        CircleInfo()
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