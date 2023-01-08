package dev.timatifey.posanie.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.timatifey.posanie.api.*
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

import dev.timatifey.posanie.api.Constants.Companion.BASE_URL
import dev.timatifey.posanie.model.domain.Teacher


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

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
