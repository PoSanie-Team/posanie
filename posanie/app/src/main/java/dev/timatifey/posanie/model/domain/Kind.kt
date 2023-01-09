package dev.timatifey.posanie.model.domain

enum class Kind(val id: Long) {
    BACHELOR(0),
    MASTER(1),
    SPECIALIST(2),
    POSTGRADUATE(3);

    companion object {
        val DEFAULT = BACHELOR

        fun kindBy(id: Long) : Kind {
            return when(id) {
                0L -> BACHELOR
                1L -> MASTER
                2L -> SPECIALIST
                3L -> POSTGRADUATE
                else -> DEFAULT
            }
        }
    }
}