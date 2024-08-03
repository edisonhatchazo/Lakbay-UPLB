package com.example.classschedule.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.MapData
import com.example.classschedule.data.MapDataRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.maplibre.android.geometry.LatLng

class LocationViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val mapDataRepository: MapDataRepository
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
    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng> = _currentLocation

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    fun getCurrentLocationOnce() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    _currentLocation.postValue(LatLng(it.latitude, it.longitude))
                }
            }
        } catch (e: SecurityException) {
            // Handle exception
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
