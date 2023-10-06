package fi.metropolia.homeweather.ui.views

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import fi.metropolia.homeweather.R

@Composable
fun QRScreen() {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val iconId = "inlineStar"
    val text = buildAnnotatedString {
        append(stringResource(R.string.make_our_day_by_leaving_us_stars))
        appendInlineContent(iconId, "[icon]")
    }

    val inlineContent = mapOf(
        Pair(
            iconId,
            InlineTextContent(
                Placeholder(
                    width = 30.sp,
                    height = 30.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "star",
                    tint = Color.Yellow,
                )
            }
        )
    )
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        LaunchedEffect(key1 = bitmap) {
            bitmap = generateQRCode()
        }

        bitmap?.let {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(align = Alignment.CenterVertically)

            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .clip(RectangleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = stringResource(R.string.qr_code_content_description)

                    )
                }
                Column {
                    Text(
                        text = stringResource(R.string.scan_for_more_info),
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(text = text,
                        modifier = Modifier.padding(10.dp),
                        inlineContent = inlineContent,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(text = "Thank you!",
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

private fun generateQRCode(): Bitmap {
    val appGithubPageURL = "https://github.com/giaongo/HomeWeather"
    // initialize multi-format writer for QR code
    val writer = MultiFormatWriter()

    // BitMatrix class to encode the entered text and set width and height
    val matrix = writer.encode(appGithubPageURL, BarcodeFormat.QR_CODE, 900, 900)

    val encoder = BarcodeEncoder()
    return encoder.createBitmap(matrix)
}

@Preview(showBackground = true)
@Composable
fun QRScreenPreview() {
   QRScreen()
}