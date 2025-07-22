package com.lalo.pixdev.data.local.dao

import androidx.room.*
import com.lalo.pixdev.data.local.entity.RequirementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequirementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(requirement: RequirementEntity)

    @Update
    suspend fun updateRequirement(requirement: RequirementEntity)

    @Delete
    suspend fun deleteRequirement(requirement: RequirementEntity)

    @Query("DELETE FROM requirements WHERE id = :requirementId")
    suspend fun deleteRequirementById(requirementId: String)

    @Query("SELECT * FROM requirements WHERE projectId = :projectId ORDER BY fechaLimite ASC")
    fun getRequirementsForProject(projectId: String): Flow<List<RequirementEntity>>

    @Query("SELECT * FROM requirements WHERE id = :requirementId")
    fun getRequirementById(requirementId: String): Flow<RequirementEntity?>
}