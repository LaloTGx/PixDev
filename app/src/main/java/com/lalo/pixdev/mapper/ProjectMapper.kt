package com.lalo.pixdev.mapper

import com.lalo.pixdev.data.local.entity.ProjectEntity
import com.lalo.pixdev.viewmodel.model.Project

fun ProjectEntity.toProject() = Project(
    id = this.id,
    name = this.name,
    dateCreated = this.dateCreated,
    isPinned = this.isPinned
)

fun Project.toEntity() = ProjectEntity(
    id = this.id,
    name = this.name,
    dateCreated = this.dateCreated,
    isPinned = this.isPinned
)
