package dev.timatifey.posanie.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SettingsUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoGroup(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : SettingsUiState

    data class HasGroup(
        val group: Group,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : SettingsUiState
}

private data class SettingsViewModelState(
    val group: Group? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): SettingsUiState =
        if (group == null) {
            SettingsUiState.NoGroup(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            SettingsUiState.HasGroup(
                group = group,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(

) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsViewModelState(isLoading = true))

    val uiState: StateFlow<SettingsUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )
}