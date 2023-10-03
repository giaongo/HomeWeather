package fi.metropolia.homeweather.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.metropolia.homeweather.dataclass.VoiceAlert
import fi.metropolia.homeweather.ui.theme.bluetooth_connected_card_bg
import fi.metropolia.homeweather.viewmodels.AlertScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AlertScreen() {
    val alertScreenViewModel = AlertScreenViewModel()
    val alertList: List<VoiceAlert>? by alertScreenViewModel.alertData.observeAsState(null)
    var expandText by remember { mutableStateOf(false) }
    var expandDivider by remember { mutableStateOf(false) }
    val textAlpha = remember { Animatable(0f) }
    val dividerAlpha = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val fadeInOut = TweenSpec<Float>(
        durationMillis = 500,
        easing = FastOutLinearInEasing
    )

    LaunchedEffect(Pair(expandText, expandDivider)) {
        // Delay the start of the animation
        delay(1000)

        coroutineScope.launch {
            textAlpha.animateTo(1f, animationSpec = fadeInOut)
            dividerAlpha.animateTo(1f, animationSpec = fadeInOut)
        }
        expandText = true
        expandDivider = true
    }

    Column {
        Text(text = "Indoor Environment Alert",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp).alpha(textAlpha.value))
        Divider(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .alpha(textAlpha.value),
            color = Color.Gray,
            thickness = 2.dp
        )
        LazyVerticalStaggeredGrid (
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            content = {
                items(alertList ?: listOf()) { alert ->
                    AlertCard(message = alert.message, timestamp = alert.timestamp)
                }
            }
        )
    }
}

@Composable
fun AlertCard(message: String, timestamp: String) {
    val parsedPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val parseLocalDateTemp = LocalDateTime.parse(timestamp, parsedPattern)
    val formattedTime =
        parseLocalDateTemp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .clickable {},
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Red, // Start color
                            bluetooth_connected_card_bg // End color
                        ),
                        startY = 0.0f,
                        endY = 40.0f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.Black),
                    maxLines = Int.MAX_VALUE
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertCardPreview() {
    AlertCard("test", "2021-10-12T12:12:12.000000")
}