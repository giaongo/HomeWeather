package fi.metropolia.homeweather.ui.views


import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.ui.theme.HomeWeatherTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember {
        mutableStateOf(false)
    }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 1500), label = ""
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1000)
        navController.navigate("main") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Splash(alpha = alphaAnim.value)

}

@Composable
fun Splash(alpha:Float) {
    Surface(modifier = Modifier.alpha(alpha)) {
        Box(modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
            .aspectRatio(1f)
            .border(
                10.dp,
                Brush.linearGradient(
                    listOf(
                        (MaterialTheme.colorScheme.inversePrimary),
                        MaterialTheme.colorScheme.tertiaryContainer
                    )
                ),
                CircleShape
            )
            .background(Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = stringResource(
                        R.string.splash_screen_logo))
                Text(text = "Home Weather",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                    )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 320, heightDp = 640)
fun  SplashScreenPreview() {
    HomeWeatherTheme {
        Splash(0.9f)
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, widthDp = 320, heightDp = 640)
fun  SplashScreenDarkPreview() {
    HomeWeatherTheme {
        Splash(0.9f)
    }
}
