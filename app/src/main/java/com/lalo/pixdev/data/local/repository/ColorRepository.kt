package com.lalo.pixdev.data.local.repository

import com.lalo.pixdev.viewmodel.model.Palette
import kotlinx.coroutines.flow.Flow

interface ColorRepository {
    suspend fun savePalette(palette: Palette)
    fun getPalettesForProject(projectId: String): Flow<List<Palette>>
    suspend fun deletePalette(projectId: String, paletteIndex: Int)
}