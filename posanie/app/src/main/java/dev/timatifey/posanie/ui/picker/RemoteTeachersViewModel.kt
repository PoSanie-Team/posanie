package dev.timatifey.posanie.ui.picker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.usecases.TeachersUseCase
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class RemoteTeachersUiState(
    val teachers: List<Teacher>,
    val isLoading: Boolean,
    val errorMessages: List<ErrorMessage>
)

@HiltViewModel
class RemoteTeachersViewModel @Inject constructor(
    private val teachersUseCase: TeachersUseCase
) : ViewModel() {

    private data class ViewModelState(
        val remoteTeachers: List<Teacher>? = null,
        val isLoading: Boolean = false,
        val errorMessages: List<ErrorMessage> = emptyList()
    ) {
        fun toUiState(): RemoteTeachersUiState = RemoteTeachersUiState(
            teachers = remoteTeachers ?: emptyList(),
            isLoading = isLoading,
            errorMessages = errorMessages
        )
    }

    private val viewModelState = MutableStateFlow(
        ViewModelState(
            isLoading = true
        )
    )

    val uiState: StateFlow<RemoteTeachersUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )
    val teacherNameSearchState = mutableStateOf("")

    fun fetchTeachersBy(name: String) {
        if (name == "") {
            viewModelState.update {
                it.copy(isLoading = false, remoteTeachers = emptyList())
            }
            return
        }

        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = teachersUseCase.fetchTeachersBy(name)
            viewModelState.update { state ->
                when (result) {
                    is Result.Success -> {
                        return@update state.copy(
                            remoteTeachers = result.data,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        val errorMessages = state.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        return@update state.copy(
                            remoteTeachers = emptyList(),
                            isLoading = false,
                            errorMessages = errorMessages
                        )
                    }
                }
            }
        }
    }
}