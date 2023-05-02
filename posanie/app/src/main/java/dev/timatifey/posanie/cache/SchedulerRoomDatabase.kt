package dev.timatifey.posanie.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.timatifey.posanie.model.cache.*


@Database(
    entities = [
        GroupSchedulerWeek::class,
        TeacherSchedulerWeek::class,
        SchedulerDay::class,
        Lesson::class,
        Group::class,
        Faculty::class,
        Teacher::class
    ],
    version = 5
)
@TypeConverters(
    WeekDayConverter::class,
    LongListConverter::class,
    StringListConverter::class,
    CalendarConverter::class
)
abstract class SchedulerRoomDatabase : RoomDatabase() {

    abstract fun GroupsDao(): GroupsDao
    abstract fun FacultiesDao(): FacultiesDao
    abstract fun TeachersDao(): TeachersDao
    abstract fun SchedulerDao(): SchedulerDao

    companion object {
        const val DATABASE_NAME = "scheduler_db"
    }
}
