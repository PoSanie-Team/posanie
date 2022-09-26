package dev.timatifey.posanie.model.data

enum class Type(val id: String, val prefix: String) {
    COMMON("common", ""),
    EVENING("evening", "в"),
    DISTANCE("distance", "з"),
    ANY("any", "");

    companion object {
        val DEFAULT = ANY

        fun typeBy(id: String) : Type {
            when(id) {
                "common" -> return COMMON
                "evening" -> return EVENING
                "distance" -> return DISTANCE
            }
            return DEFAULT
        }
    }
}