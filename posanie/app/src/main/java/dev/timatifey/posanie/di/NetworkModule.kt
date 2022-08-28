package dev.timatifey.posanie.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

import dev.timatifey.posanie.api.Constants.Companion.BASE_URL
import dev.timatifey.posanie.api.FacultyAPI
import dev.timatifey.posanie.api.GroupsAPI
import dev.timatifey.posanie.api.SchedulerAPI


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
    fun provideSchedulerAPI(retrofit: Retrofit): SchedulerAPI {
        return retrofit.create(SchedulerAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideGroupsAPI(): GroupsAPI {
        return GroupsAPI(Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideFacultyAPI(): FacultyAPI {
        return FacultyAPI(Dispatchers.IO)
    }

}