package fi.metropolia.homeweather.ui.views

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.metropolia.homeweather.ui.theme.HomeWeatherTheme
import fi.metropolia.homeweather.ui.theme.gradient_button_1
import fi.metropolia.homeweather.ui.theme.gradient_button_2
import fi.metropolia.homeweather.ui.views.components.BluetoothConnectedDeviceCard
import fi.metropolia.homeweather.ui.views.components.BluetoothScannedDeviceCard
import fi.metropolia.homeweather.ui.views.components.PermissionHandler
import fi.metropolia.homeweather.util.service.BluetoothLEService
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel

@SuppressLint("MissingPermission")
@Composable
 fun BluetoothScreen(bluetoothLEService: BluetoothLEService) {
    val bluetoothViewModel = viewModel<BluetoothViewModel>()
    val connectedDevice = bluetoothLEService.connectedDevice.observeAsState()
    val permissions = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    
    PermissionHandler(modifier = Modifier.fillMaxSize(), permissions = permissions, onPermissionDenied = {
        Text(text = "All permissions denied")
    }) {
        // All permissions are granted
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Connected Device
            Column {
                connectedDevice.value?.let {device ->
                    BluetoothConnectedDeviceCard(
                        bluetoothDevice = device,
                        bluetoothLEService = bluetoothLEService)
                    bluetoothViewModel.scannedLists.filter {
                        it.device.address == device.address
                    }.forEach {
                        bluetoothViewModel.scannedLists.remove(it)
                    }
                }
            }

            // Divider
            connectedDevice.value?.let {
                Divider(modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 8.dp, vertical = 15.dp),
                    color = Color.Gray,
                    thickness = 2.dp)
            }

            // Scan button and scan lists
            Button(
                onClick = {
                    bluetoothViewModel.scanDevices(bluetoothLEService.bluetoothAdapter.bluetoothLeScanner)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                gradient_button_1,
                                gradient_button_2
                            )
                        ),
                        shape = ButtonDefaults.shape
                    ),
            ) {
                Text(text = if (!bluetoothViewModel.fScanning) "START SCANNING" else "STOP SCANNING",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (bluetoothViewModel.fScanning) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .padding(vertical = 20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    trackColor = MaterialTheme.colorScheme.secondary,
                )
            }
            LazyColumn {
                items(items = bluetoothViewModel.scannedLists) {result ->
                    BluetoothScannedDeviceCard(
                        bluetoothLEService,
                        result
                    )
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

