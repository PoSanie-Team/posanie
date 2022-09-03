package dev.timatifey.posanie.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.timatifey.posanie.model.cache.Group

@Dao
interface GroupsDao {

    @Query("SELECT * FROM ${Group.TABLE_NAME} ORDER BY id ASC")
    suspend fun getGroups(): List<Group>

    @Query("SELECT * FROM ${Group.TABLE_NAME} WHERE is_picked = true ORDER BY id ASC")
    suspend fun getPickedGroups(): List<Group>

    @Query("SELECT * FROM ${Group.TABLE_NAME} WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): Group

    @Upsert(entity = Group::class)
    fun upsertGroups(list: List<Group>)

    @Upsert(entity = Group::class)
    fun upsertGroup(group: Group)

    @Query("DELETE FROM ${Group.TABLE_NAME} WHERE id = :groupId")
    fun deleteGroupById(groupId: Long)

}