package dev.timatifey.posanie.model.domain

import androidx.annotation.StringRes
import dev.timatifey.posanie.R

enum class AppColorScheme (
    val id: Int,
    @StringRes val nameId: Int
) {
    SYSTEM(0, R.string.system_scheme),
    PURPLE(1, R.string.purple_scheme),
    PINK(2, R.string.pink_scheme),
    GREEN(3, R.string.green_scheme),
    CONTRAST(4, R.string.contrast_scheme);

    companion object {
        val DEFAULT = SYSTEM

        fun getById(id: Int): AppColorScheme {
            return when(id) {
                0 -> SYSTEM
                1 -> PURPLE
                2 -> PINK
                3 -> GREEN
                4 -> CONTRAST
                else -> DEFAULT
            }
        }
    }
}