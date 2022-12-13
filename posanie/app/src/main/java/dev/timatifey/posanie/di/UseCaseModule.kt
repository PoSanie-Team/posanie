package dev.timatifey.posanie.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.timatifey.posanie.usecases.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Singleton
    @Binds
    abstract fun bindFaculties(impl: FacultiesUseCaseImpl): FacultiesUseCase

    @Singleton
    @Binds
    abstract fun bindGroups(impl: GroupsUseCaseImpl): GroupsUseCase

    @Singleton
    @Binds
    abstract fun bindTeachers(impl: TeachersUseCaseImpl): TeachersUseCase

    @Singleton
    @Binds
    abstract fun bindLessons(impl: LessonsUseCaseImpl): LessonsUseCase

    @Singleton
    @Binds
    abstract fun bindSettings(impl: SettingsUseCaseImpl): SettingsUseCase
}
