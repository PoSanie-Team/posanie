package dev.timatifey.posanie.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.timatifey.posanie.cache.GroupsDao
import dev.timatifey.posanie.cache.SchedulerDao
import dev.timatifey.posanie.cache.SchedulerRoomDatabase
import dev.timatifey.posanie.model.mappers.FacultyMapper
import dev.timatifey.posanie.model.mappers.GroupMapper
import dev.timatifey.posanie.model.mappers.GroupsLevelMapper
import dev.timatifey.posanie.model.mappers.TeacherMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MappersModule {

    @Singleton
    @Provides
    fun provideGroupsMapper(): GroupMapper {
        return GroupMapper()
    }

    @Singleton
    @Provides
    fun provideGroupsLevelMapper(): GroupsLevelMapper {
        return GroupsLevelMapper(GroupMapper())
    }

    @Singleton
    @Provides
    fun provideFacultyMapper(): FacultyMapper {
        return FacultyMapper()
    }

    @Singleton
    @Provides
    fun provideTeachersMapper(): TeacherMapper {
        return TeacherMapper()
    }

}
