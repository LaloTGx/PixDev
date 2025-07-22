package com.lalo.pixdev.data.local.dao

import androidx.room.*
import com.lalo.pixdev.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("SELECT * FROM project")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM project WHERE id = :projectId")
    fun getProjectById(projectId: String): Flow<ProjectEntity?>

    @Query("DELETE FROM project WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)
    //------------------------------------------------------
    @Query("SELECT * FROM project ORDER BY name ASC")
    fun getProjectsSortedByNameAsc(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM project ORDER BY name DESC")
    fun getProjectsSortedByNameDesc(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM project ORDER BY dateCreated DESC")
    fun getProjectsSortedByDateDesc(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM project ORDER BY dateCreated ASC")
    fun getProjectsSortedByDateAsc(): Flow<List<ProjectEntity>>
}
