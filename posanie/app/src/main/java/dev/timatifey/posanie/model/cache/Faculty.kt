package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Faculty.TABLE_NAME)
data class Faculty(
    @PrimaryKey
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String = "",
) {
    companion object {
        const val TABLE_NAME = "faculties_table"
    }
}