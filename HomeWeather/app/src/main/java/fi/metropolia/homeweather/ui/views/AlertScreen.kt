package fi.metropolia.homeweather.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import fi.metropolia.homeweather.dataclass.VoiceAlert
import fi.metropolia.homeweather.ui.theme.bluetooth_connected_card_bg
import fi.metropolia.homeweather.ui.theme.gradient_alert
import fi.metropolia.homeweather.ui.theme.md_theme_dark_onPrimaryContainer
import fi.metropolia.homeweather.ui.theme.md_theme_light_surfaceTint
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
        Text(
            text = "Indoor Environment Alert",
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
                    AlertCard(message = alert.message, timestamp = alert.timestamp, info = alert.info, alertScreenViewModel)

                }
            }
        )
    }
}

@Composable
fun AlertCard(message: String, timestamp: String, info: String, alertScreenViewModel: AlertScreenViewModel) {
    val parsedPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val parseLocalDateTemp = LocalDateTime.parse(timestamp, parsedPattern)
    val formattedTime =
        parseLocalDateTemp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .clickable {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            gradient_alert, // Start color
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                )
                Modal(fieldData = timestamp, alertScreenViewModel = alertScreenViewModel)
            }

        }
    }
}

@Composable
fun Modal(fieldData: String, alertScreenViewModel: AlertScreenViewModel) {
    var openPopup by remember { mutableStateOf(false)
    }
    var text by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var documentId by remember { mutableStateOf("") }
    LaunchedEffect(key1 = fieldData) {
        coroutineScope.launch {
            documentId = alertScreenViewModel.getDocumentId(fieldName = "timestamp", fieldData = fieldData, collectionName = "alert")
        }
    }


    Surface(onClick = { openPopup = true}) {
        Text(text = "Add Info")
    }

    if (openPopup) {
        Popup(alignment = Alignment.Center, properties = PopupProperties(focusable = true, dismissOnClickOutside = true)) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,) {
                Box(
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.9F)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    md_theme_light_surfaceTint, // Start color
                                    md_theme_dark_onPrimaryContainer // End color
                                ),
                                startY = 0.0f,
                                endY = 40.0f
                            )
                        )
                ) {
                    Column {
                        Box {
                            OutlinedTextField(
                                value = text,
                                onValueChange = { text = it },
                                label = { Text("Add Info")
                                }, modifier = Modifier
                                    .height(400.dp)
                                    .padding(20.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Row(modifier = Modifier.padding(10.dp)) {
                                Button(onClick = {
                                    coroutineScope.launch {
                                        if(documentId != "") {
                                            updateData(documentId = documentId, fieldData = text, fieldName = "info", collectionName = "alert")
                                            text = ""
                                            alertScreenViewModel.refresh()
                                        }
                                    }
                                    openPopup = false
                                }, modifier = Modifier.padding(5.dp)) {
                                    Text(text = "Add Info")

                                }
                                Button(onClick = { openPopup = false }) {
                                    Text(text = "Close")
                                }
                            }
                        }

                    }

                }


            }
        }
    }

}

fun updateData(fieldName: String, documentId: String, collectionName: String, fieldData: Any) {
    AlertScreenViewModel().updateFireBaseData(fieldName, documentId, collectionName, fieldData)
}

@Preview(showBackground = true)
@Composable
fun AlertCardPreview() {
    AlertCard("test", "2021-10-12T12:12:12.000000", info = "info", alertScreenViewModel = AlertScreenViewModel())
}