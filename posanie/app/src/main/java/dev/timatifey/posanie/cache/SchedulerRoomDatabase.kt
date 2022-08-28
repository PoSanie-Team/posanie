package dev.timatifey.posanie.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.timatifey.posanie.model.cache.DayLessonsConverter
import dev.timatifey.posanie.model.cache.Scheduler
import dev.timatifey.posanie.model.cache.SchedulerDay
import dev.timatifey.posanie.model.cache.SchedulerDaysConverter
import dev.timatifey.posanie.model.cache.SchedulerWeek
import dev.timatifey.posanie.model.cache.SchedulerWeekConverter
import dev.timatifey.posanie.model.domain.Lesson


@Database(
    entities = [Scheduler::class, SchedulerWeek::class, SchedulerDay::class, Lesson::class],
    version = 1
)
@TypeConverters(
    SchedulerWeekConverter::class,
    SchedulerDaysConverter::class,
    DayLessonsConverter::class,
)
abstract class SchedulerRoomDatabase : RoomDatabase() {

    abstract fun SchedulerDao(): SchedulerDao

    companion object {
        const val DATABASE_NAME = "scheduler_db"
    }
}