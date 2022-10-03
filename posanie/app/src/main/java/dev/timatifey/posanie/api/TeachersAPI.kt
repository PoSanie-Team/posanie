package dev.timatifey.posanie.api

import dev.timatifey.posanie.model.data.Group
import dev.timatifey.posanie.model.data.GroupsLevel
import dev.timatifey.posanie.model.data.Teacher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup

class TeachersAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getTeachers(teacherName: String) = withContext(dispatcher) {
        val teachers = mutableListOf<Teacher>()
        val url = "https://ruz.spbstu.ru/search/teacher?q=$teacherName"
        val doc = Jsoup.connect(url).get()
        var json = doc.select("footer").next().html()
        json = json.substring(json.indexOf("{"))
        val jsonArray = JSONObject(json).getJSONObject("searchTeacher").getJSONArray("data")
        for (ind in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(ind)
            val teacher = Teacher(
                id = jsonObject.getString("id").toLong(),
                name = jsonObject.getString("full_name")
            )
            teachers.add(teacher)
        }
        return@withContext teachers
    }
}