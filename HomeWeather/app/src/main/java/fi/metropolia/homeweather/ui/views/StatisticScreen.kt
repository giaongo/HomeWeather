package fi.metropolia.homeweather.ui.views


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.dataclass.Humidity
import fi.metropolia.homeweather.dataclass.Temperature
import fi.metropolia.homeweather.viewmodels.StaticScreenViewModel
import java.time.LocalDateTime


@Composable
fun StatisticScreen() {
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    val context = LocalContext.current
    val staticScreenViewModel = viewModel<StaticScreenViewModel>()
    val tempList: List<Temperature>? by staticScreenViewModel.temperatureData.observeAsState(null)
    val humidityList: List<Humidity>? by staticScreenViewModel.humidityData.observeAsState(null)
    Log.d("JaiMataDiV2", tempList.toString())

    LaunchedEffect(key1 = context) {
        staticScreenViewModel.getTemperatureData()
        staticScreenViewModel.getHumidityData()
    }

    val tempEntries = ArrayList<Entry>()
    val currentTime = LocalDateTime.now()
    tempList?.forEach { i ->
        val tempTimeStamp = LocalDateTime.parse(i.date)
        if(currentTime.dayOfYear == tempTimeStamp.dayOfYear && currentTime.dayOfMonth == tempTimeStamp.dayOfMonth) {
            val hourValue = LocalDateTime.parse(i.date).hour.toFloat()
            val entry2 = Entry(hourValue, i.tempData)
            tempEntries.add(entry2)
        }

    }

    val humidityEntries = ArrayList<Entry>()
    humidityList?.forEach { i ->
        val humidityTimeStamp = LocalDateTime.parse(i.date)
        if(currentTime.dayOfYear == humidityTimeStamp.dayOfYear && currentTime.dayOfMonth == humidityTimeStamp.dayOfMonth) {
            val hourValue = LocalDateTime.parse(i.date).hour.toFloat()
            val entry2 = Entry(hourValue, i.humidityData)
            humidityEntries.add(entry2)
        }
    }

    val titles = listOf("Temperature", "Humidity")
    Column() {
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
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth().weight(1F).padding(4.dp),
                factory = { context: Context ->
                    val view = LineChart(context)
                    view.legend.isEnabled = false
                    val dataSet = LineDataSet(tempEntries, "temp")
                    val data = LineData(dataSet)
                    view.xAxis.labelCount = 12
                    val desc = Description()
                    desc.text = "Temperature"
                    view.description = desc
                    view.data = data
                    view // return the view
                },
                update = { view ->
                    // Update the view
                    val dataSet = LineDataSet(tempEntries, "temp")
                    val data = LineData(dataSet)
                    view.xAxis.labelCount = 7
                    val desc = Description()
                    desc.text = "Temperature"
                    view.description = desc
                    view.data = data
                    view.invalidate()
                }
            )
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth().weight(1F).padding(4.dp),
                factory = { context: Context ->
                    val view = LineChart(context)
                    view.legend.isEnabled = false
                    val dataSet = LineDataSet(humidityEntries, "humidity")
                    val data = LineData(dataSet)
                    view.xAxis.labelCount = 7
                    val desc = Description()
                    desc.text = "Humidity"
                    view.description = desc
                    view.data = data
                    view // return the view
                },
                update = { view ->
                    // Update the view
                    val dataSet = LineDataSet(humidityEntries, "humidity")
                    val data = LineData(dataSet)
                    view.xAxis.labelCount = 7
                    val desc = Description()
                    desc.text = "Humidity"
                    view.description = desc
                    view.data = data
                    view.invalidate()
                }
            )
        }
    }
}
