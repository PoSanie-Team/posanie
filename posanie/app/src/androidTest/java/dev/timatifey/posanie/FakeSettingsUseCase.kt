package dev.timatifey.posanie

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.model.domain.Language
import dev.timatifey.posanie.usecases.SettingsUseCase
import javax.inject.Inject

class FakeSettingsUseCaseImpl @Inject constructor(
    @ApplicationContext val context: Context
) : SettingsUseCase {
    override suspend fun saveAndPickTheme(theme: AppTheme): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getTheme(): Result<AppTheme> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAndPickColorScheme(colorScheme: AppColorScheme): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getColorScheme(): Result<AppColorScheme> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAndPickLanguage(language: Language): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getLanguage(): Result<Language> {
        TODO("Not yet implemented")
    }

}