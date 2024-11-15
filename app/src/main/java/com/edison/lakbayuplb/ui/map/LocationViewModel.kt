package com.edison.lakbayuplb.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.MapData
import com.edison.lakbayuplb.data.MapDataRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.osmdroid.util.GeoPoint

class LocationViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    mapDataRepository: MapDataRepository
) : AndroidViewModel(application) {
    private val mapId: Int = checkNotNull(savedStateHandle[GuideMapDestination.MAPDATAIDARG])
    val uiState: StateFlow<MapDataUiState> =
        mapDataRepository.getMapData(mapId)
            .filterNotNull()
            .map {
                MapDataUiState(mapDataDetails = it.toMapDataDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MapDataUiState()
            )
    private val _currentLocation = MutableLiveData<GeoPoint>()
    val currentLocation: LiveData<GeoPoint> = _currentLocation

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    fun getCurrentLocationOnce() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    _currentLocation.postValue(GeoPoint(it.latitude, it.longitude))
                }
            }
        } catch (e: SecurityException) {
            Log.e("Error","$e")
        }
    }

    fun stopLocationUpdates() {
        // No-op since we're only requesting location once
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class MapDataUiState(
    val mapDataDetails: MapDataDetails = MapDataDetails()
)

data class MapDataDetails(
    val mapId: Int = 0,
    val title: String = "",
    val latitude: Double = 14.16747822735461,
    val longitude: Double = 121.24338486047947,
    val snippet: String = " ",
)


fun MapData.toMapDataDetails(): MapDataDetails = MapDataDetails(
    mapId = mapId,
    title = title,
    latitude = latitude,
    longitude = longitude,
    snippet = snippet
)
