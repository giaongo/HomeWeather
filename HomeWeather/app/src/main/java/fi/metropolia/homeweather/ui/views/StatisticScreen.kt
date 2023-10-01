package fi.metropolia.homeweather.ui.views


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fi.metropolia.homeweather.dataclass.Humidity
import fi.metropolia.homeweather.dataclass.Temperature
import fi.metropolia.homeweather.viewmodels.StaticScreenViewModel
import java.time.LocalDateTime


@Composable
fun StatisticScreen() {
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
    tempList?.forEach { i ->
        val time = LocalDateTime.parse(i.date).dayOfWeek.value.toFloat()
        val entry2 = Entry(time, i.tempData)
        tempEntries.add(entry2)
    }

    val humidityEntries = ArrayList<Entry>()
    humidityList?.forEach { i ->
        val time = LocalDateTime.parse(i.date).dayOfWeek.value.toFloat()
        val entry2 = Entry(time, i.humidityData)
        humidityEntries.add(entry2)
    }





    Column {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth().weight(1F),
            factory = { context: Context ->
                val view = LineChart(context)
                view.legend.isEnabled = false
                val dataSet = LineDataSet(tempEntries, "temp")
                val data = LineData(dataSet)
                view.xAxis.labelCount = 7
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

        AndroidView(
            modifier = Modifier
                .fillMaxWidth().weight(1F),
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
