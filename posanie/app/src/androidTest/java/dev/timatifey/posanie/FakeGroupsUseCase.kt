package dev.timatifey.posanie

import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.usecases.GroupsUseCase

class FakeGroupsUseCase: GroupsUseCase {
    override suspend fun getLocalGroups(): Result<Map<Int, GroupsLevel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPickedGroup(): Result<Group> {
        return Result.Error(Exception("No picked groups"))
    }

    override suspend fun fetchGroupsBy(facultyId: Long): Result<Map<Int, GroupsLevel>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAndPickGroup(group: Group): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun pickGroup(group: Group?): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroup(group: Group): Result<Boolean> {
        TODO("Not yet implemented")
    }
}