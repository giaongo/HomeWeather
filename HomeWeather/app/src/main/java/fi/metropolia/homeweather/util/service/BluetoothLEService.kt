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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.dataclass.Humidity
import fi.metropolia.homeweather.dataclass.Temperature
import fi.metropolia.homeweather.repository.AppRepository
import fi.metropolia.homeweather.ui.views.MainActivity
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.BLUETOOTH_SERVICE_ID
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.BLUETOOTH_TAG
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.CLIENT_CHARACTERISTIC_CONFIG_UUID
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.HUMIDITY_MEASUREMENT_UUID
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.SENSOR_SERVICE_UUID
import fi.metropolia.homeweather.viewmodels.BluetoothViewModel.Companion.TEMPERATURE_MEASUREMENT_UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.coroutines.resume
import kotlin.random.Random

@SuppressLint("MissingPermission")
class BluetoothLEService: Service() {
    lateinit var bluetoothAdapter:BluetoothAdapter
    private val binder = LocalBinder()
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var serviceNotification: Notification

    private var _connectedDevice = MutableLiveData<BluetoothDevice?>()
    val connectedDevice: LiveData<BluetoothDevice?> = _connectedDevice

    private var _temperature = MutableLiveData<Temperature?>()
    val temperature: LiveData<Temperature?> = _temperature

    private var _humidity = MutableLiveData<Humidity?>()
    val humidity: LiveData<Humidity?> = _humidity

    private lateinit var voiceAlertService:VoiceAlertService
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val firebaseUploadScope = CoroutineScope(Dispatchers.IO)

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
                    _connectedDevice.postValue(gatt?.device)
                    gatt?.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(BLUETOOTH_TAG, "disconnected GATT service")
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
                if (gattService.uuid == SENSOR_SERVICE_UUID) {
                    val currentGattService = gatt.getService(SENSOR_SERVICE_UUID)

                    // get temperature characteristic
                    val characteristicTemperature = currentGattService
                        .getCharacteristic(TEMPERATURE_MEASUREMENT_UUID)

                    // enable temperature notification
                    enableCharacteristicNotification(gatt, characteristicTemperature)

                }
            }
        }

        // update characteristic
        @Deprecated("Deprecated in Java")
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
            if(status == BluetoothGatt.GATT_SUCCESS && !ENABLE_MOCK) {
                val decodedValue = characteristic?.let { decodeSensorByteArray(it.value) }
                when(characteristic?.uuid) {
                    TEMPERATURE_MEASUREMENT_UUID -> {
                        Log.d(BLUETOOTH_TAG,"temperature value is $decodedValue")
                        voiceAlertService.raiseAlertForIndoor(temperature = decodedValue)
                        _temperature.postValue(
                            Temperature(
                                decodedValue ?: 0.0f,
                                LocalDateTime.now().toString()
                            )
                        )
                    }

                    HUMIDITY_MEASUREMENT_UUID -> {
                        Log.d(BLUETOOTH_TAG,"humidity value is $decodedValue")
                        voiceAlertService.raiseAlertForIndoor(humidity = decodedValue)
                        _humidity.postValue(Humidity(
                            decodedValue ?: 0.0f,
                            LocalDateTime.now().toString()
                        ))
                        gatt?.readCharacteristic(gatt.getService(SENSOR_SERVICE_UUID).getCharacteristic(TEMPERATURE_MEASUREMENT_UUID))
                    }
                }
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                if(descriptor?.characteristic == gatt?.getService(SENSOR_SERVICE_UUID)?.getCharacteristic(
                        TEMPERATURE_MEASUREMENT_UUID)) {
                    Log.d(BLUETOOTH_TAG,"onDescriptorWrite temperature finished")

                    // get humidity characteristic
                    val characteristicHumidity = gatt?.getService(SENSOR_SERVICE_UUID)
                        ?.getCharacteristic(HUMIDITY_MEASUREMENT_UUID)

                    // enable humidity notification
                    gatt?.let { characteristicHumidity?.let { it1 ->
                        enableCharacteristicNotification(it,
                            it1
                        )
                    } }
                } else {
                    Log.d(BLUETOOTH_TAG,"onDescriptorWrite humidity finished")
                }
            }
        }
    }

    /**
     * Initialize bluetoothAdapter and voiceAlertService
     */
    private fun initialize() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        VoiceAlertService(this).also { voiceAlertService = it }
    }

    /**
     * Connect BLE sensor
     */
    @SuppressLint("MissingPermission")
    fun connectBLE(device: BluetoothDevice, context: Context) {
        Log.d(BLUETOOTH_TAG,"connect ble called")
        try {
            bluetoothGatt = device.connectGatt(context, false,gattClientCallback)
        } catch (exception: Exception) {
            Log.e(BLUETOOTH_TAG, "unable to connect ${exception.localizedMessage}")
        }
    }

    /**
     * Disconnect BLE sensor
     */
    fun disconnectBLE() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        _connectedDevice.postValue(null)
    }

    /**
     * Gives random sensor value for temperature and humidity
     */
    private fun mockSensorData() {
        Log.d(BLUETOOTH_TAG, "mock sensor data is called")
        val temperatureRandom = Random.nextDouble(18.0, 28.0).toFloat()
        val humidityRandom = Random.nextDouble(30.0,60.0).toFloat()
        val currentTime = LocalDateTime.now().toString()
        voiceAlertService.raiseAlertForIndoor(temperature = temperatureRandom, humidity = humidityRandom)
        _temperature.postValue(Temperature(temperatureRandom,currentTime))
        _humidity.postValue(Humidity(humidityRandom, currentTime))
    }

    /**
     * function to decode sensor byte array data from Bluetooth stream
     */
    private fun decodeSensorByteArray(fourBytes: ByteArray): Float{
        return java.lang.Float.intBitsToFloat(
            (fourBytes[3].toInt() and 0xFF shl 24) or
                    (fourBytes[2].toInt() and 0xFF shl 16) or
                    (fourBytes[1].toInt() and 0xFF shl 8) or
                    (fourBytes[0].toInt() and 0xFF)
        )
    }

    /**
     * Set up characteristic notification
     */
    private fun enableCharacteristicNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
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
    }

    /**
     * create a foreground notification to inform the user about the running state of service
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

    /**
     * suspend function to get the first value of LiveData
     */
    private suspend fun <T>LiveData<T>.awaitFirstValue(): T = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val observer = Observer<T> {
                    value -> if (isActive) continuation.resume(value)
            }
            observeForever(observer)
            continuation.invokeOnCancellation {
                removeObserver(observer)
            }
        }
    }

    /**
     * upload sensor data to firebase
     */
    private fun <T> uploadData(collection:String, data: LiveData<T>) {
        try {
            firebaseUploadScope.launch {
                async {
                    Log.d("timerTask", "Upload $collection data")
                    data.value?.let {
                        AppRepository.postFirebaseData(collection, it)
                    }
                }.await()
            }
        } catch (error: Exception) {
            Log.e("timerTask", "Error in uploading $collection data ${error.localizedMessage}")
        }
    }

    //-----------------------------Service Overridden Functions----------------------------------//
    override fun onBind(p0: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Log.d(BLUETOOTH_TAG, "Bluetooth service is created")
        initialize()

        // if mock is enabled, provides mocked sensor value every 10 seconds
        if(ENABLE_MOCK) {
            Timer().scheduleAtFixedRate(timerTask {
                mockSensorData()
            },0L, MINUTE_INTERVAL)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(BLUETOOTH_TAG, "Bluetooth service is started")
        serviceNotification = addNotification(this)
        startForeground(BLUETOOTH_SERVICE_ID, serviceNotification)

        // await for first livedata value appears and upload sensor data to firebase every hour
        serviceScope.launch {
            val firstTemperatureValue = _temperature.awaitFirstValue()
            val firstHumidityValue = _humidity.awaitFirstValue()
            Log.d("timerTask", "firstTemp $firstTemperatureValue firstHumidity $firstHumidityValue")
            Timer().scheduleAtFixedRate(timerTask {
                uploadData("temperature", temperature)
                uploadData("humidity", humidity)
            },0L, HOUR_INTERVAL)
        }

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        disconnectBLE()
        voiceAlertService.shutdown()
        serviceScope.cancel()
        firebaseUploadScope.cancel()
    }

    inner class LocalBinder: Binder() {
        // get the current instance of service class
        fun getService(): BluetoothLEService = this@BluetoothLEService
    }


    companion object {
        const val ENABLE_MOCK: Boolean = false
        const val HOUR_INTERVAL:Long = 3600000L
        const val MINUTE_INTERVAL:Long = 60000L
    }

}