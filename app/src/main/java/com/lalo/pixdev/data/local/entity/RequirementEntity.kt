package com.lalo.pixdev.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "requirements",
    foreignKeys = [ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RequirementEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val nombre: String,
    val prioridad: String,
    val fechaLimite: Long,
    val completado: Boolean,
    val fechaCreacion: Long
)