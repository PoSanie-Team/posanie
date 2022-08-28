package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
