package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Teacher.TABLE_NAME)
data class Teacher(
    @PrimaryKey
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "is_picked")
    val isPicked: Boolean = false,
) {
    companion object {
        const val TABLE_NAME = "teachers_table"
    }
}