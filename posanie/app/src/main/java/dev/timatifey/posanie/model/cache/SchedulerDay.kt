package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.timatifey.posanie.ui.scheduler.WeekDay

@Entity(tableName = SchedulerDay.TABLE_NAME)
data class SchedulerDay(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    @ColumnInfo(name = "week_day")
    val weekDay: WeekDay,
    @ColumnInfo(name = "lessons")
    val lessons: List<Long>
) {
    companion object {
        const val TABLE_NAME = "scheduler_days_table"
    }
}

class WeekDayConverter {

    @TypeConverter
    fun fromWeekDayToInt(weekDay: WeekDay): Int {
        return weekDayToCalendarFormat(weekDay.ordinal)
    }

    @TypeConverter
    fun fromIntToWeekDay(weekDayOrdinal: Int): WeekDay {
        return WeekDay.getWorkDayByOrdinal(weekDayOrdinal)
    }

    private fun weekDayToCalendarFormat(weekDayOrdinal: Int) =
        if (weekDayOrdinal == 6) 1 else weekDayOrdinal + 2
}