package com.lalo.pixdev.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.lalo.pixdev.data.local.entity.PaletteEntity
import com.lalo.pixdev.viewmodel.model.Palette

object ColorMapper {

    fun Color.toArgbInt(): Int {
        return this.toArgb()
    }

    fun Int.toComposeColor(): Color {
        return Color(this)
    }

    fun List<Color>.toArgbIntList(): List<Int> {
        return this.map { it.toArgbInt() }
    }

    fun List<Int>.toComposeColorList(): List<Color> {
        return this.map { it.toComposeColor() }
    }

    fun PaletteEntity.toDomain(): Palette {
        return Palette(
            id = this.id,
            projectId = this.projectId,
            colors = listOf(
                this.color1.toComposeColor(),
                this.color2.toComposeColor(),
                this.color3.toComposeColor(),
                this.color4.toComposeColor(),
                this.color5.toComposeColor()
            ),
            paletteIndex = this.paletteIndex
        )
    }

    fun Palette.toEntity(): PaletteEntity {
        val colorsList = this.colors.take(5).toArgbIntList() +
                List(5 - this.colors.size) { Color.Transparent.toArgbInt() }

        return PaletteEntity(
            id = this.id,
            projectId = this.projectId,
            color1 = colorsList[0],
            color2 = colorsList[1],
            color3 = colorsList[2],
            color4 = colorsList[3],
            color5 = colorsList[4],
            paletteIndex = this.paletteIndex
        )
    }
}