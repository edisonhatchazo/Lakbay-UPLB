package com.example.classschedule.data

import kotlinx.coroutines.flow.Flow

class OfflinePinsRepository(private val pinsDao: PinsDao): PinsRepository {
    override fun getAllPins(): Flow<List<Pins>> = pinsDao.getAllPins()

    override fun getPin(id: Int): Flow<Pins?> = pinsDao.getPin(id)

    override suspend fun insertPin(pins: Pins) = pinsDao.insert(pins)

    override suspend fun updatePin(pins: Pins) = pinsDao.update(pins)

    override suspend fun deletePin(pins: Pins) = pinsDao.delete(pins)

}