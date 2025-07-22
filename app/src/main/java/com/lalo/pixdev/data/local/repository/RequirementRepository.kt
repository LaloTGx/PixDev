package com.lalo.pixdev.data.local.repository

import com.lalo.pixdev.viewmodel.model.Requirement
import kotlinx.coroutines.flow.Flow

interface RequirementRepository {
    suspend fun insertRequirement(requirement: Requirement)
    suspend fun updateRequirement(requirement: Requirement)
    suspend fun deleteRequirement(requirement: Requirement)
    suspend fun deleteRequirementById(requirementId: String)
    fun getRequirementsForProject(projectId: String): Flow<List<Requirement>>
    fun getRequirementById(requirementId: String): Flow<Requirement?>
}