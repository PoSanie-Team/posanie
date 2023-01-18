package dev.timatifey.posanie.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.timatifey.posanie.model.cache.*

@Dao
interface SchedulerDao {

    // Week
    @Query("SELECT * FROM ${GroupSchedulerWeek.TABLE_NAME} WHERE id = :groupId")
    suspend fun getSchedulerWeekByGroupId(groupId: Long): GroupSchedulerWeek

    @Query("SELECT * FROM ${TeacherSchedulerWeek.TABLE_NAME} WHERE id = :teacherId")
    suspend fun getSchedulerWeekByTeacherId(teacherId: Long): TeacherSchedulerWeek

    @Upsert(entity = GroupSchedulerWeek::class)
    fun upsertGroupSchedulerWeek(groupSchedulerWeek: GroupSchedulerWeek)

    @Upsert(entity = TeacherSchedulerWeek::class)
    fun upsertTeacherSchedulerWeek(teacherSchedulerWeek: TeacherSchedulerWeek)

    @Query("DELETE FROM ${GroupSchedulerWeek.TABLE_NAME} WHERE id = :groupId")
    fun deleteSchedulerWeekByGroupId(groupId: Long)

    @Query("DELETE FROM ${TeacherSchedulerWeek.TABLE_NAME} WHERE id = :teacherId")
    fun deleteSchedulerWeekByTeacherId(teacherId: Long)

    // Days
    @Query("SELECT * FROM ${SchedulerDay.TABLE_NAME} WHERE id IN (:schedulerDayIds)")
    fun getSchedulersDaysByIds(schedulerDayIds: List<Long>): List<SchedulerDay>

    @Upsert(entity = SchedulerDay::class)
    fun upsertSchedulerDay(schedulerDay: SchedulerDay)

    @Query("DELETE FROM ${SchedulerDay.TABLE_NAME} WHERE id IN (:schedulerDayIds)")
    fun deleteSchedulersDayByIds(schedulerDayIds: List<Long>)

    // Lessons
    @Query("SELECT * FROM ${Lesson.TABLE_NAME} WHERE id IN (:lessonIds)")
    fun getLessonsByIds(lessonIds: List<Long>): List<Lesson>

    @Upsert(entity = Lesson::class)
    fun upsertLessons(lessons: List<Lesson>)

    @Query("DELETE FROM ${Lesson.TABLE_NAME} WHERE id IN (:lessonIds)")
    fun deleteLessonsByIds(lessonIds: List<Int>)
}