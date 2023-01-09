package dev.timatifey.posanie.model.domain

import androidx.annotation.StringRes
import dev.timatifey.posanie.R

enum class AppTheme (
    val id: Int,
    @StringRes val nameId: Int
) {
    SYSTEM(0, R.string.system_theme),
    LIGHT(1, R.string.light_theme),
    DARK(2, R.string.dark_theme);

    companion object {
        val DEFAULT = SYSTEM

        fun getById(id: Int): AppTheme {
            return when(id) {
                0 -> SYSTEM
                1 -> LIGHT
                2 -> DARK
                else -> DEFAULT
            }
        }
    }
}