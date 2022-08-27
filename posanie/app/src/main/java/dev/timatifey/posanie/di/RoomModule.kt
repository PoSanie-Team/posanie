package dev.timatifey.posanie.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import dev.timatifey.posanie.cache.FacultiesDao
import dev.timatifey.posanie.cache.GroupsDao
import dev.timatifey.posanie.cache.SchedulerDao
import dev.timatifey.posanie.cache.SchedulerRoomDatabase

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideSchedulerDb(@ApplicationContext context: Context): SchedulerRoomDatabase {
        return Room.databaseBuilder(
            context,
            SchedulerRoomDatabase::class.java,
            SchedulerRoomDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideSchedulerDao(schedulerRoomDatabase: SchedulerRoomDatabase): SchedulerDao {
        return schedulerRoomDatabase.SchedulerDao()
    }

    @Singleton
    @Provides
    fun provideGroupsDao(schedulerRoomDatabase: SchedulerRoomDatabase): GroupsDao {
        return schedulerRoomDatabase.GroupsDao()
    }

    @Singleton
    @Provides
    fun provideFacultiesDao(schedulerRoomDatabase: SchedulerRoomDatabase): FacultiesDao {
        return schedulerRoomDatabase.FacultiesDao()
    }
}
