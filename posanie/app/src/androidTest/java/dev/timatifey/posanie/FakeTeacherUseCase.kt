package dev.timatifey.posanie

import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.usecases.TeachersUseCase

class FakeTeacherUseCase: TeachersUseCase {
    override suspend fun getLocalTeachers(): Result<List<Teacher>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPickedTeacher(): Result<Teacher> {
        return Result.Error(Exception("No picked teacher"))
    }

    override suspend fun fetchTeachersBy(name: String): Result<List<Teacher>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAndPickTeacher(teacher: Teacher): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pickTeacher(teacher: Teacher?): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTeacher(teacher: Teacher): Result<Boolean> {
        TODO("Not yet implemented")
    }
}