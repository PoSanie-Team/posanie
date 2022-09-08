package dev.timatifey.posanie.api

import android.util.Log
import dev.timatifey.posanie.model.data.Group

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup

class GroupsAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getGroupsList(facultyId: Long, selectedKind: Long) = withContext(dispatcher) {
        val groups = mutableListOf<Group>()
        val url = "https://ruz.spbstu.ru/faculty/$facultyId/groups/"
        val doc = Jsoup.connect(url).get()
        var json = doc.select("footer").next().html()
        json = json.substring(json.indexOf("{"))
        val data = JSONObject(json).getJSONObject("groups").getJSONObject("data")
        data.keys().forEach { key ->
            val jsonArray = data.optJSONArray(key) ?: return@forEach
            for (ind in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.optJSONObject(ind)
                val kindId = jsonObject.getString("kind").toLong()
                if (kindId != selectedKind) continue
                val group = Group(
                    id = jsonObject.getString("id").toLong(),
                    title = jsonObject.getString("name"),
                )
                groups.add(group)
            }
        }
        return@withContext groups
    }

}
