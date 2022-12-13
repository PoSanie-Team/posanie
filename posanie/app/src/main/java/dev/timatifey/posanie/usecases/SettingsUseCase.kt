package dev.timatifey.posanie.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import javax.inject.Inject

interface SettingsUseCase {
    suspend fun saveAndPickTheme(isDark: Boolean)
    suspend fun getTheme(): Result<Boolean>
}

class SettingsUseCaseImpl @Inject constructor(
    @ApplicationContext val context: Context
) : SettingsUseCase {

    override suspend fun saveAndPickTheme(isDark: Boolean) {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPref.edit().putBoolean(context.getString(R.string.preference_dark_theme), isDark).apply()
    }

    override suspend fun getTheme(): Result<Boolean> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        return Result.Success(sharedPref.getBoolean(context.getString(R.string.preference_dark_theme), false))
    }
}