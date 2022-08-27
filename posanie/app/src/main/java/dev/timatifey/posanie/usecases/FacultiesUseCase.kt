package dev.timatifey.posanie.usecases

import dev.timatifey.posanie.api.FacultiesAPI
import dev.timatifey.posanie.cache.FacultiesDao
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.model.mappers.FacultyMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FacultiesUseCase {
    suspend fun getLocalFaculties(): Result<List<Faculty>>
    suspend fun fetchFaculties(): Result<List<Faculty>>
}

class FacultiesUseCaseImpl @Inject constructor(
    private val facultyMapper: FacultyMapper,
    private val facultiesDao: FacultiesDao,
    private val facultiesAPI: FacultiesAPI,
) : FacultiesUseCase {

    override suspend fun getLocalFaculties(): Result<List<Faculty>> =
        withContext(Dispatchers.IO) {
            try {
                return@withContext Result.Success(
                    facultiesDao.getFaculties()
                        .map { facultyMapper.cacheToDomain(it) }
                )
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun fetchFaculties(): Result<List<Faculty>> =
        withContext(Dispatchers.IO) {
            try {
                val faculties = facultiesAPI.getFacultiesList()
                facultiesDao.upsertFaculties(
                    faculties.map { facultyMapper.dataToCache(it) }
                )
                return@withContext Result.Success(
                    faculties.map { facultyMapper.dataToDomain(it) }
                )
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

}