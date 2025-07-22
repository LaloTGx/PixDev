package com.lalo.pixdev.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "palettes")
data class PaletteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: String,
    val color1: Int,
    val color2: Int,
    val color3: Int,
    val color4: Int,
    val color5: Int,
    val paletteIndex: Int
)