package dev.timatifey.posanie.ui.scheduler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface SchedulerUiState {

    val calendar: Calendar
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoGroup(
        override val calendar: Calendar,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : SchedulerUiState

    data class HasGroup(
        val group: Group,
        override val calendar: Calendar,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : SchedulerUiState
}

private data class SchedulerViewModelState(
    val group: Group? = null,
    val calendar: Calendar? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): SchedulerUiState =
        if (group == null) {
            SchedulerUiState.NoGroup(
                calendar = calendar ?: Calendar.getInstance(),
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            SchedulerUiState.HasGroup(
                calendar = calendar ?: Calendar.getInstance(),
                group = group,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

@HiltViewModel
class SchedulerViewModel @Inject constructor(

) : ViewModel() {

    private val viewModelState = MutableStateFlow(SchedulerViewModelState(isLoading = true))

    val uiState: StateFlow<SchedulerUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun setDate(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            viewModelState.update {
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                return@update it.copy(calendar = calendar)
            }
        }
    }
}