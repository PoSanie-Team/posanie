package dev.timatifey.posanie.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.timatifey.posanie.model.cache.Lesson

@Dao
interface LessonsDao {

    @Query("SELECT * FROM ${Lesson.TABLE_NAME} ORDER BY id ASC")
    suspend fun getLessons(): List<Lesson>

    @Query("SELECT * FROM ${Lesson.TABLE_NAME} WHERE id = :groupId")
    suspend fun getLessonById(groupId: Long): Lesson

    @Upsert(entity = Lesson::class)
    fun upsertLessons(list: List<Lesson>)

    @Upsert(entity = Lesson::class)
    fun upsertLesson(group: Lesson)

    @Query("DELETE FROM ${Lesson.TABLE_NAME} WHERE id = :groupId")
    fun deleteGroupById(groupId: Long)

}