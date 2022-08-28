package dev.timatifey.posanie.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.timatifey.posanie.model.cache.Scheduler

@Dao
interface SchedulerDao {

    @Query("SELECT * FROM ${Scheduler.TABLE_NAME} WHERE group_id = :groupId ORDER BY id ASC")
    suspend fun getSchedulersForGroup(groupId: Long): List<Scheduler>

    @Upsert(entity = Scheduler::class)
    suspend fun upsertSchedulers(schedulers: List<Scheduler>)

    @Query("DELETE FROM ${Scheduler.TABLE_NAME}")
    suspend fun deleteAllUsers()

}