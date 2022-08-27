package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.timatifey.posanie.model.domain.Lesson

@Entity(tableName = Scheduler.TABLE_NAME)
data class Scheduler(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "group_id")
    val groupId: Long,
    @ColumnInfo(name = "week")
    val weekData: SchedulerWeek = SchedulerWeek(),
    @ColumnInfo(name = "days")
    val daysData: List<SchedulerDay> = listOf(),
) {
    companion object {
        const val TABLE_NAME = "scheduler_table"
    }
}

@Entity(tableName = SchedulerWeek.TABLE_NAME)
data class SchedulerWeek(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "date_start")
    val dateStart: String = "",
    @ColumnInfo(name = "date_end")
    val dateEnd: String = "",
    @ColumnInfo(name = "is_odd")
    val isOdd: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "scheduler_week_table"
    }
}

@Entity(tableName = SchedulerDay.TABLE_NAME)
data class SchedulerDay(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "weekday")
    val weekday: Int = 0,
    @ColumnInfo(name = "date")
    val date: String = "",
    @ColumnInfo(name = "lessons")
    val lessonsData: List<Lesson> = listOf()
) {
    companion object {
        const val TABLE_NAME = "scheduler_day_table"
    }
}

class SchedulerWeekConverter() {

    private val gson by lazy { GsonBuilder().create() }

    @TypeConverter
    fun fromWeekToString(week: SchedulerWeek): String {
        return gson.toJson(week)
    }

    @TypeConverter
    fun fromStringToWeek(week: String): SchedulerWeek {
        return gson.fromJson(week, SchedulerWeek::class.java)
    }

}

class SchedulerDaysConverter() {

    private val gson by lazy { GsonBuilder().create() }
    private val type = object : TypeToken<List<SchedulerDay>>() {}.type

    @TypeConverter
    fun fromListToString(days: List<SchedulerDay>): String {
        return gson.toJson(days, type)
    }

    @TypeConverter
    fun fromStringToList(days: String): List<SchedulerDay> {
        return gson.fromJson(days, type)
    }

}

class DayLessonsConverter() {

    private val gson by lazy { GsonBuilder().create() }
    private val type = object : TypeToken<List<Lesson>>() {}.type

    @TypeConverter
    fun fromListToString(lessons: List<Lesson>): String {
        return gson.toJson(lessons, type)
    }

    @TypeConverter
    fun fromStringToList(lessons: String): List<Lesson> {
        return gson.fromJson(lessons, type)
    }

}
