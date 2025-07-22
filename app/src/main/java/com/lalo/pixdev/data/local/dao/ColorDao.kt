package com.lalo.pixdev.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.lalo.pixdev.data.local.entity.PaletteEntity

@Dao
interface ColorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPalette(palette: PaletteEntity)

    @Query("SELECT * FROM palettes WHERE projectId = :projectId ORDER BY paletteIndex ASC")
    fun getPalettesForProject(projectId: String): Flow<List<PaletteEntity>>

    @Query("DELETE FROM palettes WHERE projectId = :projectId AND paletteIndex = :paletteIndex")
    suspend fun deletePalette(projectId: String, paletteIndex: Int)
}