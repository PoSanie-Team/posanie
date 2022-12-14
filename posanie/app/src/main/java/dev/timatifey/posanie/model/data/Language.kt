package dev.timatifey.posanie.model.data

enum class Language (
    val id: Int,
    val originName: String,
    val englishName: String
) {
    ENGLISH(0,"English", "English"),
    RUSSIAN(1, "Русский", "Russian");

    companion object {
        fun getById(id: Int): Language {
            return when(id) {
                1 -> RUSSIAN
                else -> ENGLISH // english is default
            }
        }
    }
}