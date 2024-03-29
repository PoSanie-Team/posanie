package dev.timatifey.posanie.usecases

import android.content.res.Resources.NotFoundException
import dev.timatifey.posanie.api.LessonsAPI
import dev.timatifey.posanie.cache.SchedulerDao
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.cache.GroupSchedulerWeek
import dev.timatifey.posanie.model.cache.SchedulerDay
import dev.timatifey.posanie.model.cache.SchedulerWeek
import dev.timatifey.posanie.model.cache.TeacherSchedulerWeek
import dev.timatifey.posanie.model.domain.Lesson
import dev.timatifey.posanie.model.mappers.LessonMapper
import dev.timatifey.posanie.ui.scheduler.WeekDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

typealias DayToLessonsMap = Map<WeekDay, List<Lesson>>

interface LessonsUseCase {
    suspend fun getLessonsByGroupId(groupId: Long): Result<DayToLessonsMap>
    suspend fun getLessonsByTeacherId(teacherId: Long): Result<DayToLessonsMap>
    suspend fun saveGroupLessons(
        groupId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap,
    ): Result<Boolean>
    suspend fun saveTeacherLessons(
        teacherId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap
    ): Result<Boolean>
    suspend fun getGroupSchedulerWeekOddness(groupId: Long): Result<Boolean>
    suspend fun getTeacherSchedulerWeekOddness(teacherId: Long): Result<Boolean>
    suspend fun getGroupSchedulerWeekMonday(groupId: Long): Result<Calendar>
    suspend fun getTeacherSchedulerWeekMonday(teacherId: Long): Result<Calendar>
    suspend fun fetchLessonsByGroupId(groupId: Long, date: String): Result<DayToLessonsMap>
    suspend fun fetchLessonsByTeacherId(teacherId: Long, date: String): Result<DayToLessonsMap>
    suspend fun fetchWeekOddnessByGroupId(groupId: Long, date: String): Result<Boolean>
    suspend fun fetchWeekOddnessByTeacherId(groupId: Long, date: String): Result<Boolean>
}

private enum class SchedulerType {
    GROUP, TEACHER
}


class LessonsUseCaseImpl @Inject constructor(
    private val lessonMapper: LessonMapper,
    private val schedulerDao: SchedulerDao,
    private val lessonsApi: LessonsAPI,
) : LessonsUseCase {

    @Suppress("USELESS_ELVIS")
    override suspend fun getLessonsByGroupId(groupId: Long): Result<DayToLessonsMap> =
        withContext(Dispatchers.IO) {
            val schedulerWeek = schedulerDao.getSchedulerWeekByGroupId(groupId)
                ?: return@withContext Result.Error(NotFoundException()) // can return null
            return@withContext getLessonsFromSchedulerWeek(schedulerWeek)
        }

    @Suppress("USELESS_ELVIS")
    override suspend fun getLessonsByTeacherId(teacherId: Long): Result<DayToLessonsMap> =
        withContext(Dispatchers.IO) {
            val schedulerWeek = schedulerDao.getSchedulerWeekByTeacherId(teacherId)
                ?: return@withContext Result.Error(NotFoundException()) // can return null
            return@withContext getLessonsFromSchedulerWeek(schedulerWeek)
        }

    override suspend fun saveGroupLessons(
        groupId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val schedulerDays = createSchedulerDays(
                weekId = groupId,
                schedulerType = SchedulerType.GROUP,
                dayToLessonsMap = dayToLessonsMap
            )
            saveLessons(dayToLessonsMap)
            saveSchedulerDays(schedulerDays)
            schedulerDao.upsertGroupSchedulerWeek(
                GroupSchedulerWeek(
                    id = groupId,
                    isOdd = if (weekIsOdd) 1 else 0,
                    mondayDate = mondayDate,
                    schedulerDays = schedulerDays.map { it.id }
                )
            )
            return@withContext Result.Success(true)
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    override suspend fun saveTeacherLessons(
        teacherId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val schedulerDays = createSchedulerDays(
                weekId = teacherId,
                schedulerType = SchedulerType.TEACHER,
                dayToLessonsMap = dayToLessonsMap
            )
            saveLessons(dayToLessonsMap)
            saveSchedulerDays(schedulerDays)
            schedulerDao.upsertTeacherSchedulerWeek(
                TeacherSchedulerWeek(
                    id = teacherId,
                    isOdd = if (weekIsOdd) 1 else 0,
                    mondayDate = mondayDate,
                    schedulerDays = schedulerDays.map { it.id }
                )
            )
            return@withContext Result.Success(true)
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    private suspend fun saveLessons(dayToLessonsMap: DayToLessonsMap) =
        withContext(Dispatchers.IO) {
            for (weekDay in dayToLessonsMap.keys) {
                val lessons = dayToLessonsMap[weekDay] ?: continue
                schedulerDao.upsertLessons(lessons.map { lessonMapper.domainToCache(it) })
            }
        }

    private fun createSchedulerDays(
        schedulerType: SchedulerType,
        weekId: Long,
        dayToLessonsMap: DayToLessonsMap
    ): List<SchedulerDay> = buildList {
        for (weekDay in dayToLessonsMap.keys) {
            val lessons = dayToLessonsMap[weekDay] ?: continue
            add(SchedulerDay(
                id = createSchedulerDayId(
                    schedulerType = schedulerType,
                    weekId = weekId,
                    weekDay = weekDay
                ),
                weekDay = weekDay,
                lessons = lessons.map { it.id }
            ))
        }
    }

    private suspend fun saveSchedulerDays(schedulerDays: List<SchedulerDay>) =
        withContext(Dispatchers.IO) {
            schedulerDays.forEach { schedulerDay ->
                schedulerDao.upsertSchedulerDay(schedulerDay)
            }
        }

    private fun createSchedulerDayId(
        schedulerType: SchedulerType,
        weekId: Long,
        weekDay: WeekDay
    ): Long {
        val typePrefix = when (schedulerType) {
            SchedulerType.GROUP -> 1
            SchedulerType.TEACHER -> 2
        }
        return "$typePrefix$weekId${weekDay.ordinal}".toLong()
    }

    @Suppress("USELESS_ELVIS")
    override suspend fun getGroupSchedulerWeekOddness(groupId: Long): Result<Boolean> =
        withContext(Dispatchers.IO) {
            val schedulerWeek = schedulerDao.getSchedulerWeekByGroupId(groupId)
                ?: return@withContext Result.Error(NotFoundException()) // can return null
            val weekIsOdd = schedulerWeek.isOdd != 0
            return@withContext Result.Success(weekIsOdd)
        }

    @Suppress("USELESS_ELVIS")
    override suspend fun getTeacherSchedulerWeekOddness(teacherId: Long): Result<Boolean> =
        withContext(Dispatchers.IO) {
            val schedulerWeek = schedulerDao.getSchedulerWeekByTeacherId(teacherId)
                ?: return@withContext Result.Error(NotFoundException()) // can return null
            val weekIsOdd = schedulerWeek.isOdd != 0
            return@withContext Result.Success(weekIsOdd)
        }

    @Suppress("USELESS_ELVIS")
    override suspend fun getGroupSchedulerWeekMonday(groupId: Long): Result<Calendar> =
        withContext(Dispatchers.IO) {
            val schedulerWeek = schedulerDao.getSchedulerWeekByGroupId(groupId)
                ?: return@withContext Result.Error(NotFoundException()) // can return null
            return@withContext Result.Success(schedulerWeek.mondayDate)
        }

    @Suppress("USELESS_ELVIS")
    override suspend fun getTeacherSchedulerWeekMonday(teacherId: Long): Result<Calendar> =
        withContext(Dispatchers.IO) {
            val schedulerWeek = schedulerDao.getSchedulerWeekByTeacherId(teacherId)
                ?: return@withContext Result.Error(NotFoundException()) // can return null
            return@withContext Result.Success(schedulerWeek.mondayDate)
        }

    private suspend fun getLessonsFromSchedulerWeek(schedulerWeek: SchedulerWeek): Result<DayToLessonsMap> =
        withContext(Dispatchers.IO) {
            val schedulerDays = schedulerDao.getSchedulersDaysByIds(schedulerWeek.schedulerDays)
            val dayToLessonsMap = mutableMapOf<WeekDay, List<Lesson>>()
            schedulerDays.forEach { schedulerDay ->
                val lessons = schedulerDao.getLessonsByIds(schedulerDay.lessons)
                dayToLessonsMap[schedulerDay.weekDay] =
                    lessons.map { lessonMapper.cacheToDomain(it) }
            }
            return@withContext Result.Success(dayToLessonsMap)
        }

    override suspend fun fetchLessonsByGroupId(
        groupId: Long,
        date: String
    ): Result<DayToLessonsMap> = withContext(Dispatchers.IO) {
        try {
            val dayToLessonsMap = lessonsApi.getLessonsByGroupId(groupId, date)
            val result = mutableMapOf<WeekDay, List<Lesson>>()
            dayToLessonsMap.forEach { (day, lessons) ->
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
    ): Result<DayToLessonsMap> = withContext(Dispatchers.IO) {
        try {
            val dayToLessonsMap = lessonsApi.getLessonsByTeacherId(teacherId, date)
            val result = mutableMapOf<WeekDay, List<Lesson>>()
            dayToLessonsMap.forEach { (day, lessons) ->
                result[day] = lessons.map { lesson -> lessonMapper.dataToDomain(lesson) }
            }
            return@withContext Result.Success(result)
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    override suspend fun fetchWeekOddnessByGroupId(
        groupId: Long,
        date: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            return@withContext Result.Success(lessonsApi.isWeekOddByGroupId(groupId, date))
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    override suspend fun fetchWeekOddnessByTeacherId(
        groupId: Long,
        date: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            return@withContext Result.Success(lessonsApi.isWeekOddByTeacherId(groupId, date))
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }
}
