package dev.timatifey.posanie.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.timatifey.posanie.model.cache.*


@Database(
    entities = [
        Scheduler::class,
        SchedulerWeek::class,
        SchedulerDay::class,
        Lesson::class,
        Group::class,
        Faculty::class,
        Teacher::class
    ],
    version = 1
)
@TypeConverters(
    SchedulerWeekConverter::class,
    SchedulerDaysConverter::class,
    DayLessonsConverter::class,
)
abstract class SchedulerRoomDatabase : RoomDatabase() {

    abstract fun SchedulerDao(): SchedulerDao
    abstract fun GroupsDao(): GroupsDao
    abstract fun FacultiesDao(): FacultiesDao
    abstract fun TeachersDao(): TeachersDao

    companion object {
        const val DATABASE_NAME = "scheduler_db"
    }
}