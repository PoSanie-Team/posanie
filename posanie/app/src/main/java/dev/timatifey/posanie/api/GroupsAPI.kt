package dev.timatifey.posanie.api

import dev.timatifey.posanie.model.domain.Group
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup

class GroupsAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getGroupsList(facultyId: Long) = withContext(dispatcher) {
        val groups = mutableListOf<Group>()
        val url = "https://ruz.spbstu.ru/faculty/$facultyId/groups/"
        val doc = Jsoup.connect(url).get()
        var json = doc.select("footer").next().html()
        json = json.substring(json.indexOf("{"))
        val data = JSONObject(json).getJSONObject("groups").getJSONObject("data")
        data.getJSONArray(data.keys().next()).let { jsonArray ->
            for (ind in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.optJSONObject(ind)
                groups.add(
                    Group(
                        id = jsonObject.getString("id").toLong(),
                        title = jsonObject.getString("name"),
                    )
                )
            }
        }
        return@withContext groups
    }

}
