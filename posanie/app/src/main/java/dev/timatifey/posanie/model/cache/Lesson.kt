package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Lesson.TABLE_NAME)
data class Lesson(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    @ColumnInfo(name = "start")
    val start: String,
    @ColumnInfo(name = "end")
    val end: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "place")
    val place: String,
    @ColumnInfo(name = "teacher")
    val teacher: String,
    @ColumnInfo(name = "lms_url")
    val lmsUrl: String
) {
    companion object {
        const val TABLE_NAME = "lesson_table"
    }
}