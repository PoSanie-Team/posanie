package dev.timatifey.posanie.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.timatifey.posanie.usecases.FacultiesUseCase
import dev.timatifey.posanie.usecases.FacultiesUseCaseImpl
import dev.timatifey.posanie.usecases.GroupsUseCase
import dev.timatifey.posanie.usecases.GroupsUseCaseImpl
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

}
