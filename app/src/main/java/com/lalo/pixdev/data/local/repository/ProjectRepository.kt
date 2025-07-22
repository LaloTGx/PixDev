package com.lalo.pixdev.data.local.repository

import com.lalo.pixdev.viewmodel.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getProjectById(projectId: String): Flow<Project?>
    suspend fun insertProject(project: Project)
    suspend fun deleteProject(projectId: String)
    suspend fun updateProject(project: Project)

    fun getProjectsSortedByNameAsc(): Flow<List<Project>>
    fun getProjectsSortedByNameDesc(): Flow<List<Project>>
    fun getProjectsSortedByDateDesc(): Flow<List<Project>>
    fun getProjectsSortedByDateAsc(): Flow<List<Project>>
}