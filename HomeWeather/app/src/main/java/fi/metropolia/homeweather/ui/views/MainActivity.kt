package fi.metropolia.homeweather.ui.views

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.metropolia.homeweather.ui.theme.HomeWeatherTheme
import fi.metropolia.homeweather.ui.views.components.MainApp
import fi.metropolia.homeweather.util.service.BluetoothLEService
import fi.metropolia.homeweather.viewmodels.AppViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start and bind Bluetooth Background Service
        Intent(applicationContext,BluetoothLEService::class.java).apply {
            startForegroundService(this)
            bindService(this, serviceConnect, Context.BIND_AUTO_CREATE)
        }

        setContent {
            HomeWeatherTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("main") {
                            MainApp(appViewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unbind bluetooth service
        unbindService(serviceConnect)
    }

    /** Defines callbacks for service binding, passed to bindService().  */
    private val serviceConnect: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            // bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as BluetoothLEService.LocalBinder
            appViewModel.updateServiceLiveData(binder.getService())
        }
        override fun onServiceDisconnected(clasName: ComponentName?) {
            appViewModel.updateServiceLiveData(null)
        }
    }
}
