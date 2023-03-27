package dev.timatifey.posanie

import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.usecases.DayToLessonsMap
import dev.timatifey.posanie.usecases.LessonsUseCase
import java.util.*

class FakeLessonsUseCase: LessonsUseCase {
    override suspend fun getLessonsByGroupId(groupId: Long): Result<DayToLessonsMap> {
        TODO("Not yet implemented")
    }

    override suspend fun getLessonsByTeacherId(teacherId: Long): Result<DayToLessonsMap> {
        TODO("Not yet implemented")
    }

    override suspend fun saveGroupLessons(
        groupId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTeacherLessons(
        teacherId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupSchedulerWeekOddness(groupId: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getTeacherSchedulerWeekOddness(teacherId: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupSchedulerWeekMonday(groupId: Long): Result<Calendar> {
        TODO("Not yet implemented")
    }

    override suspend fun getTeacherSchedulerWeekMonday(teacherId: Long): Result<Calendar> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchLessonsByGroupId(
        groupId: Long,
        date: String
    ): Result<DayToLessonsMap> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchLessonsByTeacherId(
        teacherId: Long,
        date: String
    ): Result<DayToLessonsMap> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchWeekOddnessByGroupId(groupId: Long, date: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchWeekOddnessByTeacherId(groupId: Long, date: String): Result<Boolean> {
        TODO("Not yet implemented")
    }
}