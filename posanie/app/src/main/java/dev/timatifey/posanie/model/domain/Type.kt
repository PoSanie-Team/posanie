package dev.timatifey.posanie.model.domain

enum class Type(val id: String, val prefix: String) {
    COMMON("common", ""),
    EVENING("evening", "в"),
    DISTANCE("distance", "з"),
    ANY("any", "");

    companion object {
        val DEFAULT = ANY

        fun typeBy(id: String) : Type {
            return when(id) {
                "common" -> COMMON
                "evening" -> EVENING
                "distance" -> DISTANCE
                else -> DEFAULT
            }
        }
    }
}