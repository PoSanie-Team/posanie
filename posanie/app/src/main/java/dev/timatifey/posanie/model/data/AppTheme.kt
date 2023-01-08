package dev.timatifey.posanie.model.data

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
        fun getById(id: Int): AppTheme {
            return when(id) {
                1 -> LIGHT
                2 -> DARK
                else -> SYSTEM // system is default
            }
        }
    }
}