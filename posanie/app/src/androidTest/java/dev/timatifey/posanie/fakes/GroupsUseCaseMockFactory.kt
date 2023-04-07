package dev.timatifey.posanie.fakes

import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.usecases.GroupsUseCase
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

object GroupsUseCaseMockFactory {

    fun create(): GroupsUseCase {
        val groupsUseCaseMock = mock<GroupsUseCase> {
            onBlocking { getPickedGroup() } doReturn Result.Success(getFakePickedGroup())
        }
        return groupsUseCaseMock
    }

    private fun getFakePickedGroup() = Group(
        id = 35469,
        title = "3530901/90202",
        kindId = 0,
        typeId = "common",
        level = 4,
        isPicked = true
    )
}
