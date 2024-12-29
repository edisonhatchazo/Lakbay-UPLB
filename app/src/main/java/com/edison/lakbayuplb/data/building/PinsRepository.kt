package com.edison.lakbayuplb.data.building

import kotlinx.coroutines.flow.Flow

interface PinsRepository {
    fun getAllPins(): Flow<List<Pins>>

    fun getPin(id: Int): Flow<Pins?>

    suspend fun insertPin(pins: Pins)

    suspend fun deletePin(pins: Pins)

    suspend fun updatePin(pins: Pins)
}
