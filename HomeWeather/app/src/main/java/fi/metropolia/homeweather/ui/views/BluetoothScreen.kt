package fi.metropolia.homeweather.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import fi.metropolia.homeweather.ui.theme.HomeWeatherTheme
import fi.metropolia.homeweather.ui.theme.Typography
import fi.metropolia.homeweather.util.service.BluetoothLEService
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel

@SuppressLint("MissingPermission")
@Composable
 fun BluetoothScreen(bluetoothLEService: BluetoothLEService) {
    val bluetoothViewModel = viewModel<BluetoothViewModel>()
    val permissions = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    PermissionHandler(permissions = permissions, onPermissionDenied = {
        Text(text = "All permissions denied")
    }) {
        // All permissions are granted
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BluetoothDeviceCard(25,"Test Device","FC:23:32:23:42:12", true)
            Button(onClick = {
                bluetoothViewModel.scanDevices(bluetoothLEService.bluetoothAdapter.bluetoothLeScanner)
            }) {
                Text(text = "Start Scanning")
            }
            LazyColumn {
                items(items = bluetoothViewModel.scannedLists) {result ->
                    BluetoothDeviceCard(result.rssi,result.device.name ?: "No Name",result.device.address, false)
                }
            }
        }
    }

 }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissions: List<String>,
    onPermissionDenied: @Composable () -> Unit,
    content: @Composable () -> Unit
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
            // Show rationale if necessary
            // You can use AlertDialog or any other UI component here
            // And request permission again upon user's action
        }
        else -> {
            // Permission denied
            onPermissionDenied()
        }
    }
}


@Composable
fun BluetoothDeviceCard(signalPower:Int, deviceName:String, deviceAddress:String, connectedState:Boolean) {
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
                    Text(text = "$signalPower dPm",
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
                Text(text = deviceName,
                    modifier = Modifier.padding(vertical = 5.dp),
                    style = Typography.titleSmall,
                    maxLines = 2)
                Text(text = deviceAddress, modifier = Modifier.padding(vertical = 5.dp))
                Button(onClick = { /*TODO*/ },
                    modifier = Modifier
                        .align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        if (connectedState) MaterialTheme.colorScheme.onTertiaryContainer
                        else MaterialTheme.colorScheme.primary)

                ) {
                    Text(text = if (connectedState) "DISCONNECT" else "CONNECT")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothScreenPreview() {
    val bluetoothLEService = BluetoothLEService()
    HomeWeatherTheme {
        Surface {
            BluetoothScreen(bluetoothLEService)
        }
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode",
    widthDp = 350,
    heightDp = 180)
@Composable
fun BluetoothDeviceCardPreview() {
    HomeWeatherTheme {
        Surface {
            BluetoothDeviceCard(25,"test device","FC:23:32:23:42:12", false)
        }
    }
}

