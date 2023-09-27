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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               temperature: SensorMeasurement?,
               humidity: SensorMeasurement?) {
    val weatherAPIViewModel : WeatherAPIViewModel = viewModel()
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val temperatureData = String.format("%.2f", temperature?.value ?: 0.0f)
    val humidityData = String.format("%.2f", humidity?.value ?: 0.0f)
    val temperatureTimeStamp = temperature?.timeStamp?.format(formatter) ?: "No Time"
    val humidityTimeStamp = humidity?.timeStamp?.format(formatter) ?: "No Time"

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
        Spacer(Modifier.height(30.dp))
        LazyRow {
            item {
                weatherAPIViewModel.getWeatherData(
                    lat = 60.16,
                    long = 24.93
                )
            }
        }
        Row (modifier = modifier
                .fillMaxWidth()
        ) {
            displayTemperature(
                fraction = 0.5F,
                measureLocation = "Currently Inside",
                temp = "$temperatureData°C"
            )
            displayTemperature(
                measureLocation = "Currently Outside",
                temp = "15°C"
            )
            Button(onClick = {
                    weatherAPIViewModel.getWeatherData(
                        lat = 60.16,
                        long = 24.93
                    )
            })
            {
                Text("Fetch Weather Data")
            }
        }
        CircleInfo()
    }
}

@Composable
fun displayTemperature(fraction: Float = 1F, measureLocation: String, temp: String) {
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
        Text(text = "$temp",
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
        humidity = SensorMeasurement(100.0f, LocalDateTime.now()))
}