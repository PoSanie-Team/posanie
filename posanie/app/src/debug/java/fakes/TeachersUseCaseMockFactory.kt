package fakes

import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.usecases.TeachersUseCase
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

object TeachersUseCaseMockFactory {

    fun create(): TeachersUseCase {
        val teachersUseCaseMock = mock<TeachersUseCase> {
            onBlocking { getPickedTeacher() } doReturn Result.Error(Exception("No picked teacher"))
        }

        return teachersUseCaseMock
    }
}
