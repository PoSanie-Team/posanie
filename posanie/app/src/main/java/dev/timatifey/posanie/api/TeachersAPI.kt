package dev.timatifey.posanie.api

import dev.timatifey.posanie.api.Constants.Companion.BASE_URL
import dev.timatifey.posanie.model.data.Teacher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TeachersAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getTeachers(teacherName: String) = withContext(dispatcher) {
        val teachers = mutableListOf<Teacher>()
        val json = "$BASE_URL/search/teachers?q=$teacherName".getJsonObjectIgnoringContentType()
        if (json.isNull("teachers")) return@withContext emptyList()
        val jsonArray = json.getJSONArray("teachers")
        for (ind in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(ind)
            val teacher = Teacher(
                id = jsonObject.getString("id").toLong(),
                name = jsonObject.getString("full_name")
            )
            teachers.add(teacher)
        }
        teachers.sortBy { it.name }
        return@withContext teachers
    }
}
