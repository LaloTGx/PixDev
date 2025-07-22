package com.lalo.pixdev.data.local.repository

import com.lalo.pixdev.data.local.dao.RequirementDao
import com.lalo.pixdev.mapper.toEntity
import com.lalo.pixdev.mapper.toRequirement
import com.lalo.pixdev.viewmodel.model.Requirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequirementRepositoryImpl @Inject constructor(
    private val requirementDao: RequirementDao
) : RequirementRepository {

    override suspend fun insertRequirement(requirement: Requirement) {
        requirementDao.insertRequirement(requirement.toEntity())
    }

    override suspend fun updateRequirement(requirement: Requirement) {
        requirementDao.updateRequirement(requirement.toEntity())
    }

    override suspend fun deleteRequirement(requirement: Requirement) {
        requirementDao.deleteRequirement(requirement.toEntity())
    }

    override suspend fun deleteRequirementById(requirementId: String) {
        requirementDao.deleteRequirementById(requirementId)
    }

    override fun getRequirementsForProject(projectId: String): Flow<List<Requirement>> {
        return requirementDao.getRequirementsForProject(projectId).map { entities ->
            entities.map { it.toRequirement() }
        }
    }

    override fun getRequirementById(requirementId: String): Flow<Requirement?> {
        return requirementDao.getRequirementById(requirementId).map { entity ->
            entity?.toRequirement()
        }
    }
}