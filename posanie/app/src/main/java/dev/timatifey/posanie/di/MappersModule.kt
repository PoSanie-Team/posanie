package dev.timatifey.posanie.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.timatifey.posanie.model.mappers.*
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

    @Singleton
    @Provides
    fun provideLessonsMapper(): LessonMapper {
        return LessonMapper()
    }
}
