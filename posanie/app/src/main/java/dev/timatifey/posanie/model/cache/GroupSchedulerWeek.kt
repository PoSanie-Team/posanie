package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = GroupSchedulerWeek.TABLE_NAME)
data class GroupSchedulerWeek(
    @PrimaryKey(autoGenerate = false)
    override val id: Long = 0,
    @ColumnInfo(name = "is_odd")
    override val isOdd: Int = 0,
    @ColumnInfo(name = "scheduler_days")
    override val schedulerDays: List<Long>,
    @ColumnInfo(name = "monday_date")
    override val mondayDate: Calendar
) : SchedulerWeek(id, isOdd, schedulerDays, mondayDate) {
    companion object {
        const val TABLE_NAME = "group_scheduler_weeks_table"
    }
}

