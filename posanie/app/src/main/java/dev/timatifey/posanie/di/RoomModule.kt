package dev.timatifey.posanie.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.timatifey.posanie.cache.*
import javax.inject.Singleton

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
    fun provideGroupsDao(schedulerRoomDatabase: SchedulerRoomDatabase): GroupsDao {
        return schedulerRoomDatabase.GroupsDao()
    }

    @Singleton
    @Provides
    fun provideFacultiesDao(schedulerRoomDatabase: SchedulerRoomDatabase): FacultiesDao {
        return schedulerRoomDatabase.FacultiesDao()
    }

    @Singleton
    @Provides
    fun provideTeachersDao(schedulerRoomDatabase: SchedulerRoomDatabase): TeachersDao {
        return schedulerRoomDatabase.TeachersDao()
    }

    @Singleton
    @Provides
    fun provideSchedulerDao(schedulerRoomDatabase: SchedulerRoomDatabase): SchedulerDao {
        return schedulerRoomDatabase.SchedulerDao()
    }
}
