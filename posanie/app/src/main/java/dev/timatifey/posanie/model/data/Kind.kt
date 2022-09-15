package dev.timatifey.posanie.model.data

enum class Kind(val id: Long) {
    BACHELOR(0),
    MASTER(1),
    SPECIALIST(2),
    POSTGRADUATE(3);

    companion object {
        val DEFAULT_KIND = BACHELOR

        fun kindBy(id: Long) : Kind {
            when(id) {
                0L -> return BACHELOR
                1L -> return MASTER
                2L -> return SPECIALIST
                3L -> return POSTGRADUATE
            }
            return BACHELOR
        }
    }
}