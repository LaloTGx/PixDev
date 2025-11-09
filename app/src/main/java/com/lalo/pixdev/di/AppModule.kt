package com.lalo.pixdev.di

import android.content.Context
import androidx.room.Room
import com.lalo.pixdev.data.local.PixDevDatabase
import com.lalo.pixdev.data.local.dao.ProjectDao
import com.lalo.pixdev.data.local.dao.RequirementDao
import com.lalo.pixdev.data.local.repository.ProjectRepository
import com.lalo.pixdev.data.local.repository.ProjectRepositoryImpl
import com.lalo.pixdev.data.local.repository.RequirementRepository
import com.lalo.pixdev.data.local.repository.RequirementRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PixDevDatabase {
        return Room.databaseBuilder(
            context,
            PixDevDatabase::class.java,
            "pixdev_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProjectDao(db: PixDevDatabase): ProjectDao = db.projectDao()

    @Provides
    @Singleton
    fun provideProjectRepository(
        projectDao: ProjectDao
    ): ProjectRepository {
        return ProjectRepositoryImpl(projectDao)
    }

    @Provides
    fun provideRequirementDao(db: PixDevDatabase): RequirementDao = db.requirementDao()

    @Provides
    @Singleton
    fun provideRequirementRepository(
        requirementDao: RequirementDao
    ): RequirementRepository {
        return RequirementRepositoryImpl(requirementDao)
    }
}