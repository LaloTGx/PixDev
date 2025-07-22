package com.lalo.pixdev.data.local.repository

import com.lalo.pixdev.data.local.dao.ProjectDao
import com.lalo.pixdev.viewmodel.model.Project
import com.lalo.pixdev.mapper.toProject
import com.lalo.pixdev.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toProject() }
        }
    }

    override fun getProjectById(projectId: String): Flow<Project?> {
        return projectDao.getProjectById(projectId).map { entity ->
            entity?.toProject()
        }
    }

    override suspend fun insertProject(project: Project) {
        projectDao.insertProject(project.toEntity())
    }

    override suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectById(projectId)
    }

    override suspend fun updateProject(project: Project) {
        projectDao.updateProject(project.toEntity())
    }

    override fun getProjectsSortedByNameAsc(): Flow<List<Project>> {
        return projectDao.getProjectsSortedByNameAsc().map { entities -> entities.map { it.toProject() } }
    }

    override fun getProjectsSortedByNameDesc(): Flow<List<Project>> {
        return projectDao.getProjectsSortedByNameDesc().map { entities -> entities.map { it.toProject() } }
    }

    override fun getProjectsSortedByDateDesc(): Flow<List<Project>> {
        return projectDao.getProjectsSortedByDateDesc().map { entities -> entities.map { it.toProject() } }
    }

    override fun getProjectsSortedByDateAsc(): Flow<List<Project>> {
        return projectDao.getProjectsSortedByDateAsc().map { entities -> entities.map { it.toProject() } }
    }
}