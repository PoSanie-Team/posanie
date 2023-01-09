package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val COURSE_GROUP_DELIMITER = "/"

@Entity(tableName = Group.TABLE_NAME)
data class Group(
    @PrimaryKey
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "kindId")
    val kindId: Long = 0,
    @ColumnInfo(name = "typeId")
    val typeId: String = "",
    @ColumnInfo(name = "level")
    val level: Int,
    @ColumnInfo(name = "is_picked")
    val isPicked: Int = 0,
) {
    companion object {
        const val TABLE_NAME = "groups_table"
    }
}