package dev.timatifey.posanie.model.domain

data class Lesson(
    val id: Long = 0,
    val start: String,
    val end: String,
    val name: String,
    val type: String,
    val place: String,
    val teacher: String,
    val groupNames: List<String>,
    val lmsUrl: String
)
