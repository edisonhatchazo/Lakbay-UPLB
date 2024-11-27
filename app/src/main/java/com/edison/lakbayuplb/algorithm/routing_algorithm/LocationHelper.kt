package com.edison.lakbayuplb.algorithm.routing_algorithm

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.edison.lakbayuplb.ui.map.isGeoPointInBounds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.util.GeoPoint

class LocationHelper(context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000) // 1-second interval
        .setMinUpdateIntervalMillis(500) // Minimum interval of 1 second
        .setMaxUpdateDelayMillis(1000) // Ensure no batch delays exceed 2 seconds
        .build()
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onLocationUpdate: (Location) -> Unit, onFailure: (Exception) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    onLocationUpdate(location) // Pass each location to the callback
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                if (!locationAvailability.isLocationAvailable) {
                    onFailure(Exception("Location is not available"))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

@Composable
fun checkCurrentLocation(context: Context): Boolean{
    val northwest = GeoPoint(14.199483653098456, 121.22749500166606)
    val northeast = GeoPoint(14.199483653098456, 121.25590394224918)
    val southeast = GeoPoint(14.147189880033771, 121.25590394224918)
    val southwest = GeoPoint(14.147189880033771, 121.22749500166606)
    var userLocation by remember { mutableStateOf(GeoPoint(14.167028292342057, 121.2430246076685)) }
    val boundingBox = listOf(northwest, northeast, southeast, southwest)
    val locationHelper = remember { LocationHelper(context) }
    remember {
        locationHelper.startLocationUpdates(
            onLocationUpdate = { location ->
                userLocation = GeoPoint(location.latitude, location.longitude)
            },
            onFailure = {
                // Handle location failure if needed
            }
        )
        locationHelper // Return locationHelper for `remember`
    }
    val currentLocation = userLocation
    return isGeoPointInBounds(currentLocation, boundingBox)
}