package dev.timatifey.posanie.model.domain

data class GroupsLevel(
    val level: Int = 0,
    private val groupList: MutableList<Group> = mutableListOf()
) {

    fun add(group: Group) {
        groupList.add(group)
    }

    fun getGroups(): List<Group> {
        return groupList.toList()
    }
}