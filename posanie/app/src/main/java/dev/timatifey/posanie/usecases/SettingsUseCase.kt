package dev.timatifey.posanie.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.data.Language
import javax.inject.Inject

interface SettingsUseCase {
    suspend fun saveAndPickTheme(isDark: Boolean): Result<Boolean>
    suspend fun getTheme(): Result<Boolean>
    suspend fun saveAndPickLanguage(language: Language): Result<Boolean>
    suspend fun getLanguage(): Result<Language>
}

class SettingsUseCaseImpl @Inject constructor(
    @ApplicationContext val context: Context
) : SettingsUseCase {

    override suspend fun saveAndPickTheme(isDark: Boolean): Result<Boolean> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPref.edit().putBoolean(context.getString(R.string.preference_dark_theme), isDark).apply()
        return Result.Success(true)
    }

    override suspend fun getTheme(): Result<Boolean> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        return Result.Success(sharedPref.getBoolean(context.getString(R.string.preference_dark_theme), false))
    }

    override suspend fun saveAndPickLanguage(language: Language): Result<Boolean> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPref.edit().putInt(context.getString(R.string.preference_language), language.id).apply()
        return Result.Success(true)
    }

    override suspend fun getLanguage(): Result<Language> {
        val language = getLanguageFromPreferences(context)
        return Result.Success(language)
    }
}

fun getLanguageFromPreferences(context: Context): Language {
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )
    val languageId = sharedPref.getInt(context.getString(R.string.preference_language), 0)
    return Language.getById(languageId)
}