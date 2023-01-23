package dev.timatifey.posanie.api

import dev.timatifey.posanie.api.Constants.Companion.BASE_URL
import dev.timatifey.posanie.model.data.Faculty
import dev.timatifey.posanie.model.data.Teacher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class FacultiesAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getFacultiesList(): List<Faculty> = withContext(dispatcher) {
        val faculties = mutableListOf<Faculty>()
        val json = "$BASE_URL/faculties/".getJsonObjectIgnoringContentType()
        if (json.isNull("faculties")) return@withContext emptyList()
        val jsonArray = json.getJSONArray("faculties")
        for (ind in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(ind)
            val faculty = Faculty(
                id = jsonObject.getString("id").toLong(),
                title = jsonObject.getString("name")
            )
            faculties.add(faculty)
        }
        faculties.sortBy { it.title }
        return@withContext faculties
    }

}
