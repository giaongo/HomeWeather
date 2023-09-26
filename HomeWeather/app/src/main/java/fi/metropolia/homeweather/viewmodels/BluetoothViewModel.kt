package fi.metropolia.homeweather.viewmodels

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class BluetoothViewModel(): ViewModel() {
    var fScanning:Boolean by mutableStateOf(false)
    var scannedLists = mutableStateListOf<ScanResult>()
    val scanResults = HashMap<String, ScanResult>()
    @SuppressLint("MissingPermission")
    fun scanDevices(scanner: BluetoothLeScanner) {
        viewModelScope.launch(Dispatchers.IO){
            Log.d(BLUETOOTH_TAG, "scanDevices() is called")
            fScanning = true
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build()
            scanner.startScan(null, settings, leScanCallback)
            scannedLists.clear()
            delay(SCANNING_DURATION)
            scanner.stopScan(leScanCallback)
            scannedLists.addAll(scanResults.values)
            fScanning = false
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                val deviceAddress = it.device.address
                scanResults[deviceAddress] = it
            }

        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(BLUETOOTH_TAG,"Scan failed with error $errorCode")
        }
    }

    companion object {
        const val SCANNING_DURATION = 3000L
        const val BLUETOOTH_TAG = "APP_BLUETOOTH"
        const val BLUETOOTH_SERVICE_ID = 1
        val TEMPERATURE_SERVICE_UUID = convertFromInteger(0X180A)
        val TEMPERATURE_MEASUREMENT_UUID = convertFromInteger(0x2A57)
        val CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902)

        private fun convertFromInteger(i: Int): UUID {
            val MSB = 0x0000000000001000L
            val LSB = -0x7fffff7fa064cb05L
            val value = (i and -0x1).toLong()
            return UUID(MSB or (value shl 32), LSB)
        }
    }
}