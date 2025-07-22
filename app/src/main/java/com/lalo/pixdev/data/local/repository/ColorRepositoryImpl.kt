package com.lalo.pixdev.data.local.repository

import com.lalo.pixdev.data.local.dao.ColorDao
import com.lalo.pixdev.mapper.ColorMapper.toDomain
import com.lalo.pixdev.mapper.ColorMapper.toEntity
import com.lalo.pixdev.viewmodel.model.Palette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColorRepositoryImpl @Inject constructor(
    private val colorDao: ColorDao
) : ColorRepository {
    override suspend fun savePalette(palette: Palette) {
        colorDao.insertPalette(palette.toEntity())
    }

    override fun getPalettesForProject(projectId: String): Flow<List<Palette>> {
        return colorDao.getPalettesForProject(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deletePalette(projectId: String, paletteIndex: Int) {
        colorDao.deletePalette(projectId, paletteIndex)
    }
}