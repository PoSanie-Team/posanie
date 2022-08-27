package dev.timatifey.posanie.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel

import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.usecases.FacultiesUseCase
import dev.timatifey.posanie.utils.ErrorMessage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.util.*
import javax.inject.Inject

sealed interface FacultiesUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class FacultiesList(
        val faculties: List<Faculty>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : FacultiesUiState
}

private data class FacultiesViewModelState(
    val faculties: List<Faculty>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): FacultiesUiState =
        FacultiesUiState.FacultiesList(
            faculties = faculties.orEmpty(),
            isLoading = isLoading,
            errorMessages = errorMessages,
        )
}

@HiltViewModel
class FacultiesViewModel @Inject constructor(
    private val facultiesUseCase: FacultiesUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FacultiesViewModelState(isLoading = true))

    val uiState: StateFlow<FacultiesUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        getFaculties()
    }

    fun getFaculties() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            var result = facultiesUseCase.fetchFaculties()
            if (result is Result.Error) {
                result = facultiesUseCase.getLocalFaculties()
            }
            viewModelState.update { state ->
                when (result) {
                    is Result.Success -> {
                        state.copy(
                            faculties = result.data,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        val errorMessages = state.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        state.copy(
                            faculties = null,
                            errorMessages = errorMessages,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

}