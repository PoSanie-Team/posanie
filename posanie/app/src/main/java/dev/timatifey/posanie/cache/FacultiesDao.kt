package dev.timatifey.posanie.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.timatifey.posanie.model.cache.Faculty

@Dao
interface FacultiesDao {

    @Query("SELECT * FROM ${Faculty.TABLE_NAME} ORDER BY id ASC")
    suspend fun getFaculties(): List<Faculty>

    @Upsert(entity = Faculty::class)
    fun upsertFaculties(faculties: List<Faculty>)

    @Query("DELETE FROM ${Faculty.TABLE_NAME}")
    fun deleteFaculties()

}