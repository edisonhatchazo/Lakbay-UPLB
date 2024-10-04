package com.edison.lakbayuplb.algorithm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.Building
import com.edison.lakbayuplb.data.BuildingRepository
import com.edison.lakbayuplb.data.Classroom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val buildingRepository: BuildingRepository
) : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    val buildingSuggestions: StateFlow<List<Building>> = snapshotFlow { searchQuery }
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(emptyList())
            } else {
                buildingRepository.searchBuildings(query)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val roomSuggestions: StateFlow<List<Classroom>> = snapshotFlow { searchQuery }
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(emptyList())
            } else {
                buildingRepository.searchRooms(query)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
}