package dev.timatifey.posanie.api

import dev.timatifey.posanie.api.Constants.Companion.BASE_URL
import dev.timatifey.posanie.model.data.Group
import dev.timatifey.posanie.model.data.GroupsLevel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GroupsAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getGroups(facultyId: Long) = withContext(dispatcher) {
        val groups = mutableMapOf<Int, GroupsLevel>()
        val json = "$BASE_URL/faculties/$facultyId/groups/".getJsonObjectIgnoringContentType()
        if (json.isNull("groups")) return@withContext emptyMap()
        val jsonArray = json.getJSONArray("groups")
        for (ind in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(ind)
            val group = Group(
                id = jsonObject.getString("id").toLong(),
                title = jsonObject.getString("name"),
                kindId = jsonObject.getString("kind").toLong(),
                typeId = jsonObject.getString("type").toString(),
                level = jsonObject.getInt("level")
            )
            addGroup(groups, group)
        }
        groups.keys.forEach { key ->
            groups[key]?.sortGroups()
        }
        return@withContext groups
    }


    private fun addGroup(groups: MutableMap<Int, GroupsLevel>, group: Group) {
        if (!groups.containsKey(group.level)) {
            groups[group.level] = GroupsLevel(level = group.level)
        }
        groups[group.level]?.addGroup(group)
    }
}
