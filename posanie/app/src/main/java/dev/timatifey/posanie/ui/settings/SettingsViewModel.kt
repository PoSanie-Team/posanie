package dev.timatifey.posanie.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.model.data.AppTheme
import dev.timatifey.posanie.model.data.Language
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.successOr
import dev.timatifey.posanie.usecases.SettingsUseCase
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsUiState (
    val theme: AppTheme,
    val language: Language,
    val isLoading: Boolean,
    val errorMessages: List<ErrorMessage>
)

private data class SettingsViewModelState(
    val theme: AppTheme,
    val language: Language,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): SettingsUiState = SettingsUiState(
        theme = theme,
        language = language,
        isLoading = isLoading,
        errorMessages = errorMessages
    )
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        SettingsViewModelState(
            theme = AppTheme.SYSTEM,
            language = Language.ENGLISH,
            isLoading = false
        )
    )

    val uiState: StateFlow<SettingsUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun saveAndPickTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsUseCase.saveAndPickTheme(theme)
            getTheme()
        }
    }

    fun getTheme() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = settingsUseCase.getTheme()
            viewModelState.update { state ->
                return@update state.copy(
                    theme = result.successOr(AppTheme.SYSTEM),
                    isLoading = false,
                )
            }
        }
    }

    fun saveAndPickLanguage(language: Language) {
        viewModelScope.launch {
            settingsUseCase.saveAndPickLanguage(language)
            getLanguage()
        }
    }

    fun getLanguage() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = settingsUseCase.getLanguage()
            viewModelState.update { state ->
                return@update state.copy(
                    language = result.successOr(Language.ENGLISH),
                    isLoading = false,
                )
            }
        }
    }
}