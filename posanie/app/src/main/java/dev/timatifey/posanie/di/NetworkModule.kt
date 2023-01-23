package dev.timatifey.posanie.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.timatifey.posanie.api.FacultiesAPI
import dev.timatifey.posanie.api.GroupsAPI
import dev.timatifey.posanie.api.LessonsAPI
import dev.timatifey.posanie.api.TeachersAPI
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGroupsAPI(): GroupsAPI {
        return GroupsAPI(Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideFacultyAPI(): FacultiesAPI {
        return FacultiesAPI(Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideTeachersAPI(): TeachersAPI {
        return TeachersAPI(Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideLessonsAPI(): LessonsAPI {
        return LessonsAPI(Dispatchers.IO)
    }
}
