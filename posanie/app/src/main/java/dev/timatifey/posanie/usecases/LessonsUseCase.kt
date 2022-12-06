package dev.timatifey.posanie.usecases

import dev.timatifey.posanie.api.LessonsAPI
import dev.timatifey.posanie.cache.LessonsDao
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Lesson
import dev.timatifey.posanie.model.mappers.LessonMapper
import dev.timatifey.posanie.ui.scheduler.WeekWorkDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LessonsUseCase {
    suspend fun getLessons(): Result<List<Lesson>>
    suspend fun fetchLessonsByGroupId(groupId: Long, date: String): Result<Map<WeekWorkDay, List<Lesson>>>
    suspend fun fetchLessonsByTeacherId(teacherId: Long, date: String): Result<Map<WeekWorkDay, List<Lesson>>>
    suspend fun fetchWeekOddnessByGroupId(groupId: Long, date: String): Result<Boolean>
    suspend fun fetchWeekOddnessByTeacherId(groupId: Long, date: String): Result<Boolean>
}

class LessonsUseCaseImpl @Inject constructor(
    private val lessonMapper: LessonMapper,
    private val lessonsDao: LessonsDao,
    private val lessonsApi: LessonsAPI,
) : LessonsUseCase {
    override suspend fun getLessons(): Result<List<Lesson>> =
        withContext(Dispatchers.IO) {
            return@withContext Result.Success(
                lessonsDao.getLessons().map { lesson -> lessonMapper.cacheToDomain(lesson) }
            )
        }

    override suspend fun fetchLessonsByGroupId(
        groupId: Long,
        date: String
    ): Result<Map<WeekWorkDay, List<Lesson>>> =
        withContext(Dispatchers.IO) {
            try {
                val lessonsToDays = lessonsApi.getLessonsByGroupId(groupId, date)
                if (lessonsToDays.isEmpty()) {
                    return@withContext Result.Error(Exception())
                }
                val result = mutableMapOf<WeekWorkDay, List<Lesson>>()
                lessonsToDays.forEach { (day, lessons) ->
                    result[day] = lessons.map { lesson -> lessonMapper.dataToDomain(lesson) }
                }
                return@withContext Result.Success(result)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun fetchLessonsByTeacherId(
        teacherId: Long,
        date: String
    ): Result<Map<WeekWorkDay, List<Lesson>>> =
        withContext(Dispatchers.IO) {
            try {
                val lessonsToDays = lessonsApi.getLessonsByTeacherId(teacherId, date)
                if (lessonsToDays.isEmpty()) {
                    return@withContext Result.Error(Exception())
                }
                val result = mutableMapOf<WeekWorkDay, List<Lesson>>()
                lessonsToDays.forEach { (day, lessons) ->
                    result[day] = lessons.map { lesson -> lessonMapper.dataToDomain(lesson) }
                }
                return@withContext Result.Success(result)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun fetchWeekOddnessByGroupId(groupId: Long, date: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val isOdd = lessonsApi.weekOddnessByGroupId(groupId, date)
                return@withContext Result.Success(isOdd)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun fetchWeekOddnessByTeacherId(groupId: Long, date: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val isOdd = lessonsApi.weekOddnessByTeacherId(groupId, date)
                return@withContext Result.Success(isOdd)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
}