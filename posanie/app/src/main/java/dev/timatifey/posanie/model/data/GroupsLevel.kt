package dev.timatifey.posanie.model.data

data class GroupsLevel(
    val level: Int = 0,
    private val groupList: MutableList<Group> = mutableListOf()
) {

    fun addGroup(group: Group) {
        groupList.add(group)
    }

    fun sortGroups() {
        groupList.sortBy { it.title }
    }

    fun getGroups(): List<Group> {
        return groupList.toList()
    }
}