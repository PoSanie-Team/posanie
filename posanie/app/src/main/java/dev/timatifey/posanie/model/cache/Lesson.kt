package dev.timatifey.posanie.model.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

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
    @ColumnInfo(name = "group_names")
    val groupNames: List<String>,
    @ColumnInfo(name = "lms_url")
    val lmsUrl: String
) {
    companion object {
        const val TABLE_NAME = "lesson_table"
    }
}

class StringListConverter {

    private val gson by lazy { GsonBuilder().create() }
    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromStringListToString(list: List<String>): String {
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun fromStringToStringList(listJson: String): List<String> {
        return gson.fromJson(listJson, type)
    }

}
