package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Group.TABLE_NAME)
data class Group(
    @PrimaryKey
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "is_picked")
    val isPicked: Boolean = false,
) {
    companion object {
        const val TABLE_NAME = "groups_table"
    }
}