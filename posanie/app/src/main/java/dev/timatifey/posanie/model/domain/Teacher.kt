package dev.timatifey.posanie.model.domain

data class Teacher(
    val id: Long = 0,
    val name: String = "",
    val isPicked: Boolean = false,
) : ScheduleType