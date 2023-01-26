package dev.timatifey.posanie.usecases

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.IdRes
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

    override suspend fun saveAndPickTheme(theme: AppTheme): Result<Boolean> =
        context.putInt(R.string.preference_theme, theme.id)

    override suspend fun getTheme(): Result<AppTheme> =
        context.getInt(R.string.preference_theme, AppTheme.DEFAULT.id) { AppTheme.getById(it) }

    override suspend fun saveAndPickColorScheme(colorScheme: AppColorScheme): Result<Boolean> =
        context.putInt(R.string.preference_color_scheme, colorScheme.id)

    override suspend fun getColorScheme(): Result<AppColorScheme> =
        context.getInt(R.string.preference_color_scheme, AppColorScheme.DEFAULT.id) { AppColorScheme.getById(it) }

    override suspend fun saveAndPickLanguage(language: Language): Result<Boolean> =
        context.putInt(R.string.preference_language, language.id)

    override suspend fun getLanguage(): Result<Language> = context.getLanguageFromPreferences()
}

fun Context.getLanguageFromPreferences(): Result<Language> =
    getInt(R.string.preference_language, Language.DEFAULT.id) { languageId ->
        Language.getById(languageId)
    }

private fun <T> Context.getInt(
    @IdRes id: Int,
    defaultValue: Int,
    buildResult: (Int) -> T
): Result<T> {
    val sharedPref = getSharedPreferences()
    val intValue = sharedPref.getInt(getString(id), defaultValue)
    return Result.Success(buildResult(intValue))
}

private fun Context.putInt(@IdRes id: Int, defaultValue: Int): Result<Boolean> {
    val sharedPref = getSharedPreferences()
    sharedPref.edit().putInt(getString(id), defaultValue).apply()
    return Result.Success(true)
}

private fun Context.getSharedPreferences(): SharedPreferences = getSharedPreferences(
    getString(R.string.preference_file_key),
    Context.MODE_PRIVATE
)