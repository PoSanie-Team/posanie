package dev.timatifey.posanie.usecases

import android.util.Log
import dev.timatifey.posanie.api.GroupsAPI
import dev.timatifey.posanie.cache.GroupsDao
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.mappers.GroupMapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface GroupsUseCase {
    suspend fun getLocalGroups(): Result<List<Group>>
    suspend fun fetchGroupsBy(facultyId: Long, degreeId: Long): Result<List<Group>>
    suspend fun saveAndPickGroup(group: Group): Result<Boolean>
}

class GroupsUseCaseImpl @Inject constructor(
    private val groupMapper: GroupMapper,
    private val groupsDao: GroupsDao,
    private val groupsAPI: GroupsAPI,
) : GroupsUseCase {

    override suspend fun getLocalGroups(): Result<List<Group>> =
        withContext(Dispatchers.IO) {
            return@withContext Result.Success(
                groupsDao.getGroups()
                    .map { groupMapper.cacheToDomain(it) }
            )
        }

    override suspend fun fetchGroupsBy(facultyId: Long, degreeId: Long): Result<List<Group>> =
        withContext(Dispatchers.IO) {
            try {
                val groups = groupsAPI.getGroupsList(facultyId, degreeId)
                if (groups.isEmpty()) {
                    return@withContext Result.Error(Exception())
                }
                return@withContext Result.Success(
                    groups.map { groupMapper.dataToDomain(it) }
                )
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun saveAndPickGroup(group: Group): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val newGroups = groupsDao.getPickedGroups()
                    .map { it.copy(isPicked = false) }
                    .toMutableList()
                newGroups.add(groupMapper.domainToCache(group.copy(isPicked = true)))
                groupsDao.upsertGroups(newGroups)
                return@withContext Result.Success(true)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

}