package com.lalo.pixdev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lalo.pixdev.data.local.repository.ProjectRepository
import com.lalo.pixdev.data.local.repository.RequirementRepository
import com.lalo.pixdev.viewmodel.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.annotation.StringRes
import com.lalo.pixdev.R
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val requirementRepository: RequirementRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())

    private val _sortType = MutableStateFlow(ProjectSortType.DATE_NEWEST_FIRST)
    val sortType: StateFlow<ProjectSortType> = _sortType.asStateFlow()

    private val MAX_PROJECT_NAME_LENGTH = 15
    private val MAX_PROJECTS = 5

    val projects: StateFlow<List<Project>> =
        _projects.combine(_sortType) { projects, sortType ->
            val pinnedProjects = projects.filter { it.isPinned }.sortedByDescending { it.dateCreated }
            val unpinnedProjects = projects.filterNot { it.isPinned }.let {
                when (sortType) {
                    ProjectSortType.NAME_ASCENDING -> it.sortedBy { project -> project.name }
                    ProjectSortType.NAME_DESCENDING -> it.sortedByDescending { project -> project.name }
                    ProjectSortType.DATE_NEWEST_FIRST -> it.sortedByDescending { project -> project.dateCreated }
                    ProjectSortType.DATE_OLDEST_FIRST -> it.sortedBy { project -> project.dateCreated }
                }
            }
            pinnedProjects + unpinnedProjects
        }.stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        viewModelScope.launch {
            projectRepository.getAllProjects().collect { projectList ->
                _projects.value = projectList
            }
        }
    }

    fun addProject(name: String): Boolean {
        if (_projects.value.size >= MAX_PROJECTS) {
            return false
        }

        viewModelScope.launch {
            val newProject = Project(
                id = UUID.randomUUID().toString(),
                name = name,
                dateCreated = System.currentTimeMillis(),
                isPinned = false
            )
            projectRepository.insertProject(newProject)
        }
        return true
    }

    fun updateProject(updatedProject: Project) {
        viewModelScope.launch {
            projectRepository.updateProject(updatedProject)
        }
    }

    fun pinProject(project: Project): Boolean {
        val currentPinnedCount = _projects.value.count { it.isPinned }
        if (!project.isPinned && currentPinnedCount >= 3) {
            return false
        }
        viewModelScope.launch {
            val updatedProject = project.copy(isPinned = !project.isPinned)
            projectRepository.updateProject(updatedProject)
        }
        return true
    }

    fun sortProjectsBy(sortType: ProjectSortType) {
        _sortType.value = sortType
    }

    enum class ProjectSortType(@StringRes val titleResId: Int) {
        NAME_ASCENDING(R.string.sort_name_ascending),
        NAME_DESCENDING(R.string.sort_name_descending),
        DATE_NEWEST_FIRST(R.string.sort_date_newest_first),
        DATE_OLDEST_FIRST(R.string.sort_date_oldest_first);
    }

    fun getProjectById(projectId: String): Flow<Project?> {
        return projectRepository.getProjectById(projectId)
    }

    fun getProjectCompletionPercentage(projectId: String): Flow<Int> {
        return requirementRepository.getRequirementsForProject(projectId)
            .mapLatest { requirements ->
                if (requirements.isEmpty()) {
                    0
                } else {
                    val completedCount = requirements.count { it.completado }
                    val totalCount = requirements.size
                    ((completedCount.toFloat() / totalCount.toFloat()) * 100).toInt()
                }
            }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            projectRepository.deleteProject(project.id)
        }
    }
}