package com.lalo.pixdev.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lalo.pixdev.data.local.dao.ProjectDao
import com.lalo.pixdev.data.local.dao.RequirementDao
import com.lalo.pixdev.data.local.entity.ProjectEntity
import com.lalo.pixdev.data.local.entity.RequirementEntity
import com.lalo.pixdev.utils.DateConverter

@Database(
    entities = [ProjectEntity::class, RequirementEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class PixDevDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun requirementDao(): RequirementDao
}