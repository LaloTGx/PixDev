package com.lalo.pixdev.mapper

import com.lalo.pixdev.data.local.entity.RequirementEntity
import com.lalo.pixdev.viewmodel.model.Requirement

fun RequirementEntity.toRequirement(): Requirement {
    return Requirement(
        id = this.id,
        nombre = this.nombre,
        prioridad = this.prioridad,
        fechaLimite = this.fechaLimite,
        completado = this.completado,
        projectId = this.projectId
    )
}

fun Requirement.toEntity(): RequirementEntity {
    return RequirementEntity(
        id = this.id,
        nombre = this.nombre,
        prioridad = this.prioridad,
        fechaLimite = this.fechaLimite,
        completado = this.completado,
        projectId = this.projectId,
        fechaCreacion = this.fechaCreacion
    )
}