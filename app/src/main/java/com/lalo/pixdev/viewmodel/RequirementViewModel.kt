package com.lalo.pixdev.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lalo.pixdev.data.local.repository.RequirementRepository
import com.lalo.pixdev.viewmodel.model.Prioridad
import com.lalo.pixdev.viewmodel.model.Requirement
import com.lalo.pixdev.viewmodel.model.RequirementSortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RequirementViewModel @Inject constructor(
    private val requirementRepository: RequirementRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _rawRequirements = MutableStateFlow<List<Requirement>>(emptyList())

    private val _sortType = MutableStateFlow(RequirementSortType.DEFAULT_ORDER)
    val sortType: StateFlow<RequirementSortType> = _sortType.asStateFlow()

    private val _priorityFilter = MutableStateFlow<Prioridad?>(null)
    val priorityFilter: StateFlow<Prioridad?> = _priorityFilter.asStateFlow()

    private val _selectedRequirement = MutableStateFlow<Requirement?>(null)
    val selectedRequirement: StateFlow<Requirement?> = _selectedRequirement.asStateFlow()

    val requirements: StateFlow<List<Requirement>> =
        _rawRequirements.combine(_sortType) { rawList, sortType ->
            when (sortType) {
                RequirementSortType.DEFAULT_ORDER -> rawList.sortedByDescending { it.fechaCreacion }
                RequirementSortType.NAME_ASCENDING -> rawList.sortedBy { it.nombre }
                RequirementSortType.NAME_DESCENDING -> rawList.sortedByDescending { it.nombre }
                RequirementSortType.PRIORITY_ASCENDING -> rawList.sortedBy { it.prioridad.toPriorityLevel() }
                RequirementSortType.PRIORITY_DESCENDING -> rawList.sortedByDescending { it.prioridad.toPriorityLevel() }
            }
        }
            .combine(_priorityFilter) { sortedList, priorityFilter ->
                if (priorityFilter == null) {
                    sortedList
                } else {
                    sortedList.filter { it.prioridad.equals(priorityFilter.name, ignoreCase = true) }
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    init {
        viewModelScope.launch {
            requirementRepository.getRequirementsForProject(projectId).collectLatest { requirementsList ->
                _rawRequirements.value = requirementsList
            }
        }
    }

    fun addRequirement(nombre: String, prioridad: String, fechaLimite: Long) {
        viewModelScope.launch {
            val newRequirement = Requirement(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                nombre = nombre,
                prioridad = prioridad,
                fechaLimite = fechaLimite,
                completado = false,
                fechaCreacion = System.currentTimeMillis()
            )
            requirementRepository.insertRequirement(newRequirement)
        }
    }

    fun toggleCompletado(requirementId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val currentRequirement = requirementRepository.getRequirementById(requirementId).firstOrNull()
            currentRequirement?.let {
                val updated = it.copy(completado = isCompleted)
                requirementRepository.updateRequirement(updated)
            }
        }
    }

    fun updateRequirement(updatedRequirement: Requirement) {
        viewModelScope.launch {
            requirementRepository.updateRequirement(updatedRequirement)
        }
    }

    fun deleteRequirement(requirementId: String) {
        viewModelScope.launch {
            requirementRepository.deleteRequirementById(requirementId)
        }
    }

    fun selectRequirement(requirementId: String?) {
        viewModelScope.launch {
            _selectedRequirement.value = if (requirementId != null) {
                requirementRepository.getRequirementById(requirementId).firstOrNull()
            } else {
                null
            }
        }
    }

    fun sortRequirementsBy(sortType: RequirementSortType) {
        _sortType.value = sortType
    }

    fun filterRequirementsByPriority(priority: Prioridad?) {
        _priorityFilter.value = priority
    }
}

fun String.toPriorityLevel(): Int {
    return when (this.lowercase()) {
        Prioridad.ESSENTIAL.name.lowercase() -> 3
        Prioridad.MANDATORY.name.lowercase() -> 2
        Prioridad.OPTIONAL.name.lowercase() -> 1
        else -> 0
    }
}