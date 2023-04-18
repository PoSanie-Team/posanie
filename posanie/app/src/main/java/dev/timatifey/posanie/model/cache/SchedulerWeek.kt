package dev.timatifey.posanie.model.cache

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*

sealed class SchedulerWeek(
    open val id: Long = 0,
    open val isOdd: Int = 0,
    open val schedulerDays: List<Long>,
    open val mondayDate: Calendar
)

class LongListConverter {

    private val gson by lazy { GsonBuilder().create() }
    private val type = object : TypeToken<List<Long>>() {}.type

    @TypeConverter
    fun fromLongListToString(list: List<Long>): String {
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun fromStringToLongList(listJson: String): List<Long> {
        return gson.fromJson(listJson, type)
    }

}

class CalendarConverter {

    @TypeConverter
    fun fromCalendarToString(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return "$year-$month-$day"
    }

    @TypeConverter
    fun fromStringToIntList(calendarJson: String): Calendar {
        val strings = calendarJson.split("-")
        val year = strings[0].toInt()
        val month = strings[1].toInt()
        val day = strings[2].toInt()
        val result = Calendar.getInstance()
        result.set(year, month, day)
        return result
    }

}
