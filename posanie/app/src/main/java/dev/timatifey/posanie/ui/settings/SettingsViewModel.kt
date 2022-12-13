package dev.timatifey.posanie.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.successOr
import dev.timatifey.posanie.usecases.SettingsUseCase
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsUiState (
    val darkTheme: Boolean,
    val isLoading: Boolean,
    val errorMessages: List<ErrorMessage>
)

private data class SettingsViewModelState(
    val darkTheme: Boolean,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): SettingsUiState = SettingsUiState(
        darkTheme = darkTheme,
        isLoading = isLoading,
        errorMessages = errorMessages
    )
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsViewModelState(darkTheme = false, isLoading = false))

    val uiState: StateFlow<SettingsUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun saveAndPickTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsUseCase.saveAndPickTheme(isDark)
            getTheme()
        }
    }

    fun getTheme() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = settingsUseCase.getTheme()
            viewModelState.update { state ->
                return@update state.copy(
                    darkTheme = result.successOr(false),
                    isLoading = false,
                )
            }
        }
    }
}