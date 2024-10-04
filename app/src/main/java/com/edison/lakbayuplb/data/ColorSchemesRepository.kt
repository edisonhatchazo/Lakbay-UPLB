package com.edison.lakbayuplb.data

import kotlinx.coroutines.flow.Flow

interface ColorSchemesRepository {
    fun getAllColorSchemes(): Flow<List<ColorSchemes>>
    fun getColorSchemeById(id: Int):  Flow<ColorSchemes?>
    fun getColorSchemeByName(name: String): ColorSchemes?
    suspend fun insertColorScheme(colorScheme: ColorSchemes)
    suspend fun updateColorScheme(colorScheme: ColorSchemes)
    suspend fun deleteColorScheme(colorScheme: ColorSchemes)
    suspend fun incrementIsCurrentlyUsed(id:Int)
    suspend fun decrementIsCurrentlyUsed(id:Int)
}

class OfflineColorSchemesRepository(private val colorSchemesDao: ColorSchemesDao): ColorSchemesRepository{
    override fun getAllColorSchemes(): Flow<List<ColorSchemes>> = colorSchemesDao.getAllColorSchemes()
    override fun getColorSchemeById(id: Int): Flow<ColorSchemes?> = colorSchemesDao.getColorSchemeById(id)
    override fun getColorSchemeByName(name: String): ColorSchemes? = colorSchemesDao.getColorSchemeByName(name)
    override suspend fun insertColorScheme(colorScheme: ColorSchemes) = colorSchemesDao.insertColorScheme(colorScheme)
    override suspend fun updateColorScheme(colorScheme: ColorSchemes) = colorSchemesDao.updateColorScheme(colorScheme)
    override suspend fun deleteColorScheme(colorScheme: ColorSchemes) = colorSchemesDao.deleteColorScheme(colorScheme)
    override suspend fun incrementIsCurrentlyUsed(id: Int) = colorSchemesDao.incrementIsCurrentlyUsed(id)
    override suspend fun decrementIsCurrentlyUsed(id: Int) = colorSchemesDao.decrementIsCurrentlyUsed(id)
}
