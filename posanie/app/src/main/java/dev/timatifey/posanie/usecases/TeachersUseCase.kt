package dev.timatifey.posanie.usecases

import dev.timatifey.posanie.api.TeachersAPI
import dev.timatifey.posanie.cache.SchedulerDao
import dev.timatifey.posanie.cache.TeachersDao
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.cache.Lesson
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.model.mappers.TeacherMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface TeachersUseCase {
    suspend fun getLocalTeachers(): Result<List<Teacher>>
    suspend fun getPickedTeacher(): Result<Teacher>
    suspend fun fetchTeachersBy(name: String): Result<List<Teacher>>
    suspend fun saveAndPickTeacher(teacher: Teacher): Result<Boolean>
    suspend fun pickTeacher(teacher: Teacher?): Result<Boolean>
    suspend fun deleteTeacher(teacher: Teacher): Result<Boolean>
}

class TeachersUseCaseImpl @Inject constructor(
    private val teacherMapper: TeacherMapper,
    private val teachersDao: TeachersDao,
    private val schedulerDao: SchedulerDao,
    private val teachersAPI: TeachersAPI,
) : TeachersUseCase {

    override suspend fun getLocalTeachers(): Result<List<Teacher>> = withContext(Dispatchers.IO) {
        return@withContext Result.Success(
            teachersDao.getTeachers().map { teacher -> teacherMapper.cacheToDomain(teacher) }
        )
    }

    override suspend fun getPickedTeacher(): Result<Teacher> = withContext(Dispatchers.IO) {
        teachersDao.getTeachers().forEach {
            val isPicked = it.isPicked != 0
            if (isPicked) {
                return@withContext Result.Success(teacherMapper.cacheToDomain(it))
            }
        }
        return@withContext Result.Error(Exception("No picked teachers"))
    }

    override suspend fun fetchTeachersBy(name: String): Result<List<Teacher>> =
        withContext(Dispatchers.IO) {
            try {
                val teachers = teachersAPI.getTeachers(name)
                return@withContext Result.Success(
                    teachers.map { teacher -> teacherMapper.dataToDomain(teacher) }
                )
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun saveAndPickTeacher(teacher: Teacher): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val newTeachers = teachersDao.getPickedTeachers()
                    .map { it.copy(isPicked = 0) }
                    .toMutableList()
                newTeachers.add(teacherMapper.domainToCache(teacher.copy(isPicked = true)))
                teachersDao.upsertTeachers(newTeachers)
                return@withContext Result.Success(true)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun pickTeacher(teacher: Teacher?): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val newTeachers = teachersDao.getTeachers()
                    .map { it.copy(isPicked = if (it.id == teacher?.id) 1 else 0) }
                    .toMutableList()
                teachersDao.upsertTeachers(newTeachers)
                return@withContext Result.Success(true)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun deleteTeacher(teacher: Teacher): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                teachersDao.deleteTeacherById(teacher.id)
                deleteTeacherLessonsCache(teacher.id)
                return@withContext Result.Success(true)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    private suspend fun deleteTeacherLessonsCache(teacherId: Long) {
        val schedulerWeek = schedulerDao.getSchedulerWeekByTeacherId(teacherId)
        val schedulerDays = schedulerDao.getSchedulersDaysByIds(schedulerWeek.schedulerDays)
        val lessons = mutableListOf<Lesson>()
        schedulerDays.forEach { lessons.addAll(schedulerDao.getLessonsByIds(it.lessons)) }
        schedulerDao.deleteLessonsByIds(lessons.map { it.id })
        schedulerDao.deleteSchedulersDaysByIds(schedulerDays.map { it.id })
        schedulerDao.deleteSchedulerWeekByTeacherId(teacherId)
    }

}