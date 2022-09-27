package dev.timatifey.posanie.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.timatifey.posanie.model.cache.Teacher

@Dao
interface TeachersDao {

    @Query("SELECT * FROM ${Teacher.TABLE_NAME} ORDER BY id ASC")
    suspend fun getTeachers(): List<Teacher>

    @Query("SELECT * FROM ${Teacher.TABLE_NAME} WHERE is_picked = true ORDER BY id ASC")
    suspend fun getPickedTeachers(): List<Teacher>

    @Query("SELECT * FROM ${Teacher.TABLE_NAME} WHERE id = :teacherId")
    suspend fun getTeacherById(teacherId: Long): Teacher

    @Upsert(entity = Teacher::class)
    fun upsertTeachers(list: List<Teacher>)

    @Upsert(entity = Teacher::class)
    fun upsertTeacher(teacher: Teacher)

    @Query("DELETE FROM ${Teacher.TABLE_NAME} WHERE id = :teacherId")
    fun deleteTeacherById(teacherId: Long)

}