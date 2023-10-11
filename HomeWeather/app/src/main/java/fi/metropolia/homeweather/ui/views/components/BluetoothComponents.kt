package fi.metropolia.homeweather.ui.views.components

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.ui.theme.HomeWeatherTheme
import fi.metropolia.homeweather.ui.theme.Typography
import fi.metropolia.homeweather.ui.theme.bluetooth_connected_card_bg
import fi.metropolia.homeweather.util.service.BluetoothLEService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissions: List<String>,
    onPermissionDenied: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)
    LaunchedEffect(permissionState) {
        permissionState.launchMultiplePermissionRequest()
    }

    when {
        permissionState.allPermissionsGranted -> {
            content()
        }
        permissionState.shouldShowRationale -> {
            Toast.makeText(LocalContext.current, "Bluetooth and location permissions are required to launch the app",
                Toast.LENGTH_SHORT).show()
        }
        else -> {
            // Permission denied
            onPermissionDenied()
        }
    }
}


@Composable
fun BluetoothScannedDeviceCard(
    bluetoothLEService: BluetoothLEService,
    result: ScanResult,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(height = 200.dp)
        .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(80.dp, 80.dp)
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(text = "${result.rssi} dPm",
                    style = Typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ){
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(context,
                        stringResource(R.string.we_need_bluetooth_permission_to_continue),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Text(text = result.device.name ?: "Unknown",
                        modifier = Modifier.padding(vertical = 5.dp),
                        style = Typography.titleSmall,
                        maxLines = 2)
                    Text(text = result.device.address, modifier = Modifier.padding(vertical = 5.dp))
                    Button(onClick = {
                        coroutineScope.launch {
                            bluetoothLEService.connectBLE(result.device,context)
                        }
                    },
                        modifier = Modifier
                            .align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)

                    ) {
                        Text(text = "CONNECT")
                    }
                }

            }
        }
    }
}

@Composable
fun BluetoothConnectedDeviceCard(
    bluetoothDevice:BluetoothDevice? = null,
    bluetoothLEService: BluetoothLEService
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(height = 200.dp)
        .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bluetooth_connected_card_bg),
    ) {
        Row(modifier = Modifier
            .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ){
                if (ActivityCompat.checkSelfPermission(
                        LocalContext.current,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(LocalContext.current,
                        stringResource(R.string.we_need_bluetooth_permission_to_continue),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Text(text = bluetoothDevice?.name ?: "Unknown",
                        modifier = Modifier.padding(vertical = 5.dp),
                        style = Typography.titleSmall,
                        color = Color.Black)
                    Text(text = bluetoothDevice?.address ?: "23",
                        modifier = Modifier.padding(vertical = 5.dp),
                        color = Color.Black)
                }
            }
            Button(onClick = { bluetoothLEService.disconnectBLE()},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            ) {
                Text(text = "Disconnect", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothConnectedDeviceCardPreview() {
    HomeWeatherTheme {
        Surface {
            BluetoothConnectedDeviceCard(bluetoothLEService = BluetoothLEService())
        }
    }
}