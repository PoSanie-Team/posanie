package dev.timatifey.posanie.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.model.domain.Language
import javax.inject.Inject

interface SettingsUseCase {
    suspend fun saveAndPickTheme(theme: AppTheme): Result<Boolean>
    suspend fun getTheme(): Result<AppTheme>
    suspend fun saveAndPickColorScheme(colorScheme: AppColorScheme): Result<Boolean>
    suspend fun getColorScheme(): Result<AppColorScheme>
    suspend fun saveAndPickLanguage(language: Language): Result<Boolean>
    suspend fun getLanguage(): Result<Language>
}

class SettingsUseCaseImpl @Inject constructor(
    @ApplicationContext val context: Context
) : SettingsUseCase {

    override suspend fun saveAndPickTheme(theme: AppTheme): Result<Boolean> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPref.edit().putInt(context.getString(R.string.preference_theme), theme.id).apply()
        return Result.Success(true)
    }

    override suspend fun getTheme(): Result<AppTheme> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val themeId = sharedPref.getInt(context.getString(R.string.preference_theme), 0)
        return Result.Success(AppTheme.getById(themeId))
    }

    override suspend fun saveAndPickColorScheme(colorScheme: AppColorScheme): Result<Boolean> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPref.edit().putInt(context.getString(R.string.preference_color_scheme), colorScheme.id).apply()
        return Result.Success(true)
    }

    override suspend fun getColorScheme(): Result<AppColorScheme> {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val colorSchemeId = sharedPref.getInt(context.getString(R.string.preference_color_scheme), 0)
        return Result.Success(AppColorScheme.getById(colorSchemeId))
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