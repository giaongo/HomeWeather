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
import androidx.compose.runtime.mutableStateOf
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

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var tabIndex by remember {
        mutableStateOf(0)
    }
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
        Text(text = "this is home screen")
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
    HomeScreen()
}