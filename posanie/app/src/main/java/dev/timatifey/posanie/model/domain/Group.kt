package dev.timatifey.posanie.model.domain

data class Group(
    val id: Long = 0,
    val title: String = "",
    val kindId: Long = 0,
    val typeId: String = "",
    val level: Int = 0,
    val isPicked: Boolean = false,
)