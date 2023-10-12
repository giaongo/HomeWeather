@file:Suppress("DEPRECATION")

package fi.metropolia.homeweather.util.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import fi.metropolia.homeweather.R
import java.util.concurrent.TimeUnit

data class LatAndLong(
    var latitude: Double,
    var longitude: Double
)

@Composable
fun getUserLocation(context: Context): MutableState<LatAndLong> {
    var locationCallback: LocationCallback?
    //The main entry point for interacting with the Fused Location Provider
    val locationProvider: FusedLocationProviderClient?
    // The Fused Location Provider provides access to location APIs.
    locationProvider = LocationServices.getFusedLocationProviderClient(context)

    val userLocation = remember {
        mutableStateOf(LatAndLong(60.16 ,21.93))
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
        locationUpdate(locationProvider, locationCallback = locationCallback, context = context)
        onDispose {
            stopLocationUpdate(locationProvider, locationCallback = locationCallback)
        }
    }
    return userLocation
}

fun locationUpdate(locationProvider: FusedLocationProviderClient, locationCallback: LocationCallback?, context: Context) {
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
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context,
                    ContextCompat.getString(
                        context,
                        R.string.we_need_bluetooth_permission_to_continue
                    ), Toast.LENGTH_SHORT).show()
                return
            }
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
