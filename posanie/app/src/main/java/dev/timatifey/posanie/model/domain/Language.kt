package dev.timatifey.posanie.model.domain

enum class Language (
    val id: Int,
    val originName: String,
    val englishName: String,
    val localeName: String
) {
    ENGLISH(0,"English", "English", "en"),
    RUSSIAN(1, "Русский", "Russian", "ru");

    companion object {
        val DEFAULT = ENGLISH

        fun getById(id: Int): Language {
            return when(id) {
                0 -> ENGLISH
                1 -> RUSSIAN
                else -> DEFAULT
            }
        }
    }
}