package dev.timatifey.posanie.usecases

import android.content.res.Resources.NotFoundException
import dev.timatifey.posanie.api.GroupsAPI
import dev.timatifey.posanie.cache.GroupsDao
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.model.mappers.GroupMapper
import dev.timatifey.posanie.model.mappers.GroupsLevelMapper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GroupsUseCase {
    suspend fun getLocalGroups(): Result<Map<Int, GroupsLevel>>
    suspend fun getPickedGroup(): Result<Group>
    suspend fun fetchGroupsBy(facultyId: Long): Result<Map<Int, GroupsLevel>>
    suspend fun saveAndPickGroup(group: Group): Result<Boolean>
    suspend fun pickGroup(group: Group?): Result<Boolean>
}

class GroupsUseCaseImpl @Inject constructor(
    private val groupMapper: GroupMapper,
    private val groupsLevelMapper: GroupsLevelMapper,
    private val groupsDao: GroupsDao,
    private val groupsAPI: GroupsAPI,
) : GroupsUseCase {

    override suspend fun getLocalGroups(): Result<Map<Int, GroupsLevel>> =
        withContext(Dispatchers.IO) {
            val result = mutableMapOf<Int, GroupsLevel>()
            groupsDao.getGroups().forEach {
                if (!result.containsKey(it.level)) {
                    result[it.level] = GroupsLevel(it.level)
                }
                result[it.level]?.add(groupMapper.cacheToDomain(it))
            }
            return@withContext Result.Success(result)
        }

    override suspend fun getPickedGroup(): Result<Group> =
        withContext(Dispatchers.IO) {
            groupsDao.getGroups().forEach {
                if (it.isPicked) {
                    return@withContext Result.Success(groupMapper.cacheToDomain(it))
                }
            }
            return@withContext Result.Error(Exception("No picked groups"))
        }

    override suspend fun fetchGroupsBy(facultyId: Long): Result<Map<Int, GroupsLevel>> =
        withContext(Dispatchers.IO) {
            try {
                val groups = groupsAPI.getGroups(facultyId)
                if (groups.isEmpty()) {
                    return@withContext Result.Error(Exception())
                }
                return@withContext Result.Success(
                    groups.mapValues { entry -> groupsLevelMapper.dataToDomain(entry.value) }
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

    override suspend fun pickGroup(group: Group?): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val newGroups = groupsDao.getGroups()
                    .map { it.copy(isPicked = it.id == group?.id) }
                    .toMutableList()
                groupsDao.upsertGroups(newGroups)
                return@withContext Result.Success(true)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
}