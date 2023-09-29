package fi.metropolia.homeweather.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.metropolia.homeweather.ui.theme.bluetooth_connected_card_bg

@Composable
fun AlertScreen() {
    AlertCard()
}

@Composable
fun AlertCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(16.dp)
            .clickable {},
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red, // Start color
                            bluetooth_connected_card_bg // End color
                        ),
                        startX = 0.0f,
                        endX = 40.0f // Adjust the end position as needed
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // You can place content inside the card here
            Text(
                text = "Gradient Card",
                style = MaterialTheme.typography.titleSmall.copy(color = Color.Black)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AlertCardPreview() {
    AlertCard()
}