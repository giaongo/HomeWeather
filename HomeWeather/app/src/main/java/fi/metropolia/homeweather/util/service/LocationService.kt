import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

data class LatandLong(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)
@SuppressLint("UnrememberedMutableState")
@Composable
fun getUserLocation(context: Context): MutableState<LatandLong> {
    var locationCallback: LocationCallback? = null
    //The main entry point for interacting with the Fused Location Provider
    var locationProvider: FusedLocationProviderClient? = null
    // The Fused Location Provider provides access to location APIs.
    locationProvider = LocationServices.getFusedLocationProviderClient(context)

    var userLocation = remember {
        mutableStateOf(LatandLong(0.0 ,0.0))
    }

    DisposableEffect(key1 = locationProvider) {
        locationCallback = object : LocationCallback() {

            @SuppressLint("MissingPermission")
            override fun onLocationResult(result: LocationResult) {
                /**
                 * This returns the most recent historical location currently available.
                 * Will return null if no historical location is available
                 * */
                locationProvider.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            val lat = "%.2f".format(location.latitude).toDouble()
                            val long = "%.2f".format(location.longitude).toDouble()
                            userLocation.value.latitude = lat
                            userLocation.value.longitude = long
                        }
                    }
                    .addOnFailureListener {
                        Log.e("Location_error", "${it.message}")
                    }
            }
        }
        locationUpdate(locationProvider, locationCallback = locationCallback)
        onDispose {
            stopLocationUpdate(locationProvider, locationCallback = locationCallback)
        }
    }
    return userLocation
}
@SuppressLint("MissingPermission")
fun locationUpdate(locationProvider: FusedLocationProviderClient, locationCallback: LocationCallback?) {
    locationCallback.let {
        //An encapsulation of various parameters for requesting
        // location through FusedLocationProviderClient.
        val locationRequest: LocationRequest =
            LocationRequest.create().apply {
                interval = TimeUnit.SECONDS.toMillis(60)
                fastestInterval = TimeUnit.SECONDS.toMillis(30)
                maxWaitTime = TimeUnit.MINUTES.toMillis(2)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        //use FusedLocationProviderClient to request location update
        it?.let { it1 ->
            locationProvider.requestLocationUpdates(
                locationRequest,
                it1,
                Looper.getMainLooper()
            )
        }
    }
}

fun stopLocationUpdate(locationProvider: FusedLocationProviderClient, locationCallback: LocationCallback?) {
    try {
        //Removes all location updates for the given callback.
        val removeTask = locationCallback?.let { locationProvider.removeLocationUpdates(it) }
        removeTask?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("LOCATION_TAG", "Location Callback removed.")
            } else {
                Log.d("LOCATION_TAG", "Failed to remove Location Callback.")
            }
        }
    } catch (se: SecurityException) {
        Log.e("LOCATION_TAG", "Failed to remove Location Callback.. $se")
    }
}
