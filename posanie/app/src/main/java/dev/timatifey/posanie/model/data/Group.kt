package dev.timatifey.posanie.model.data

data class Group(
    val id: Long = 0,
    val title: String = "",
    val kindId: Long,
    val typeId: String,
    val level: Int
)