package fi.metropolia.homeweather.util.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.ui.views.MainActivity
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.BLUETOOTH_SERVICE_ID
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.BLUETOOTH_TAG
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.CLIENT_CHARACTERISTIC_CONFIG_UUID
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.TEMPERATURE_MEASUREMENT_UUID
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.TEMPERATURE_SERVICE_UUID
import java.nio.ByteBuffer
import java.nio.ByteOrder
@SuppressLint("MissingPermission")
class BluetoothLEService(): Service() {
    private val binder = LocalBinder()
    private var bluetoothGatt: BluetoothGatt? = null
    var bleConnectedState: Boolean = false
    lateinit var bluetoothAdapter:BluetoothAdapter
    private lateinit var serviceNotification: Notification

    private val gattClientCallback = object : BluetoothGattCallback() {

        // check connection state
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (status) {
                BluetoothGatt.GATT_FAILURE -> Log.d(BLUETOOTH_TAG, "GATT connection failure")
                BluetoothGatt.GATT_SUCCESS -> Log.d(BLUETOOTH_TAG, "GATT connection success")
            }
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(BLUETOOTH_TAG, "Connected GATT service")
                    bleConnectedState = true
                    gatt?.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(BLUETOOTH_TAG, "disconnected GATT service")
                    bleConnectedState = false
                }
            }
        }

        // discover service
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d(BLUETOOTH_TAG, "onServicesDiscovered failure")
                return
            }
            Log.d(BLUETOOTH_TAG, "onServicesDiscovered success")
            for (gattService in gatt.services) {
                Log.d(BLUETOOTH_TAG, "Service: ${gattService.type} ${gattService.uuid}")
                // Find temperature service
                if (gattService.uuid == TEMPERATURE_SERVICE_UUID) {
                    Log.d(BLUETOOTH_TAG, "Found temperature service")

                    // get temperature characteristic
                    val characteristic = gatt.getService(TEMPERATURE_SERVICE_UUID)
                        .getCharacteristic(TEMPERATURE_MEASUREMENT_UUID)

                    // set up notification
                    val askNotificationResult =
                        gatt.setCharacteristicNotification(characteristic, true)
                    if (askNotificationResult) {
                        val descriptor =
                            characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                        Log.d(BLUETOOTH_TAG, "Gatt notification enabled")
                    } else {
                        Log.d(BLUETOOTH_TAG, "Gatt notification not enabled")
                    }
                    // read characteristic
                    if(characteristic.isReadable()) {
                        Log.d(BLUETOOTH_TAG, "Gatt characteristic is not readable")
                        gatt.readCharacteristic(characteristic)
                    }
                }
            }
        }

        // update characteristic
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            gatt?.readCharacteristic(characteristic)
        }


        @Suppress("DEPRECATION")
        @Deprecated(
            "Used natively in Android 12 and lower",
            ReplaceWith("onCharacteristicRead(gatt, characteristic, characteristic.value, status)")
        )
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
//        onCharacteristicRead(gatt!!, characteristic!!, characteristic.value, status)
            Log.d(BLUETOOTH_TAG, "onCharacteristicRead read")
            if(status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(BLUETOOTH_TAG, "read successfully")
                val fourBytes: ByteArray = characteristic!!.value
                val numericalValue = (fourBytes[3].toInt() shl 24) +
                        (fourBytes[2].toInt() shl 16) +
                        (fourBytes[1].toInt() shl 8) +
                        (fourBytes[0].toInt())
                val nextValue = java.lang.Float.intBitsToFloat(
                    (fourBytes[3].toInt() and 0xFF shl 24) or
                            (fourBytes[2].toInt() and 0xFF shl 16) or
                            (fourBytes[1].toInt() and 0xFF shl 8) or
                            (fourBytes[0].toInt() and 0xFF)
                )
                val floatValue = ByteBuffer.wrap(fourBytes).order(ByteOrder.LITTLE_ENDIAN).float
                Log.d(BLUETOOTH_TAG,"read value is ${nextValue}")

            } else if (status == BluetoothGatt.GATT_FAILURE) {
                Log.d(BLUETOOTH_TAG, "read value")
            }
        }
    }

    /**
     * Initialize bluetoothAdapter
     */
    fun initialize() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    /**
     * check if characteristic is readable
     */
    private fun BluetoothGattCharacteristic.isReadable():Boolean = containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

    private fun BluetoothGattCharacteristic.containsProperty(property:Int):Boolean {
        return properties and property != 0
    }

    /**
     * Connect BLE sensor
     */
    @SuppressLint("MissingPermission")
    suspend fun connectBLE(device: BluetoothDevice, context: Context) {
        Log.d(BLUETOOTH_TAG,"connect ble called")
        try {
            bluetoothGatt = device.connectGatt(context, false,gattClientCallback)
        } catch (exception: Exception) {
            Log.e(BLUETOOTH_TAG, "unable to connect ${exception.localizedMessage}")
        }
    }

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Log.d(BLUETOOTH_TAG, "Bluetooth service is created")
        initialize()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(BLUETOOTH_TAG, "Bluetooth service is started")
        serviceNotification = addNotification(this)
        startForeground(BLUETOOTH_SERVICE_ID, serviceNotification)
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.let {gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }

    inner class LocalBinder: Binder() {
        fun getService(): BluetoothLEService = this@BluetoothLEService
    }

    /**
     * create a foreground notification to inform the user the running state of service
     */
    private fun addNotification(context: Context):Notification {
        val bluetoothChannel = "app_bluetooth_service"
        val serviceTitle = "Bluetooth Service"
        val pendingIntent: PendingIntent =
            Intent(context, MainActivity::class.java).let { intent ->
                PendingIntent.getActivity(context,123 , intent,
                    PendingIntent.FLAG_IMMUTABLE)
            }
        val notification = Notification.Builder(context, bluetoothChannel)
            .setContentTitle(serviceTitle)
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.bluetooth)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(bluetoothChannel,serviceTitle, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        return notification
    }
}