package dev.timatifey.posanie.usecases

import dev.timatifey.posanie.api.TeachersAPI
import dev.timatifey.posanie.cache.TeachersDao
import dev.timatifey.posanie.model.Result
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
    private val teachersAPI: TeachersAPI,
) : TeachersUseCase {

    override suspend fun getLocalTeachers(): Result<List<Teacher>> =
        withContext(Dispatchers.IO) {
            return@withContext Result.Success(
                teachersDao.getTeachers().map { teacher -> teacherMapper.cacheToDomain(teacher) }
            )
        }

    override suspend fun getPickedTeacher(): Result<Teacher> =
        withContext(Dispatchers.IO) {
            teachersDao.getTeachers().forEach {
                if (it.isPicked) {
                    return@withContext Result.Success(teacherMapper.cacheToDomain(it))
                }
            }
            return@withContext Result.Error(Exception("No picked groups"))
        }

    override suspend fun fetchTeachersBy(name: String): Result<List<Teacher>> =
        withContext(Dispatchers.IO) {
            try {
                val teachers = teachersAPI.getTeachers(name)
                if (teachers.isEmpty()) {
                    return@withContext Result.Error(Exception())
                }
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
                    .map { it.copy(isPicked = false) }
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
                    .map { it.copy(isPicked = it.id == teacher?.id) }
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
                return@withContext Result.Success(true)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

}