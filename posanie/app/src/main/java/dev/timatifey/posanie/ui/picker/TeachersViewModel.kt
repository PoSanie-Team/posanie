package dev.timatifey.posanie.ui.picker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.model.successOr
import dev.timatifey.posanie.usecases.GroupsUseCase
import dev.timatifey.posanie.usecases.TeachersUseCase
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface TeachersUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class LocalTeacherList(
        val teachers: List<Teacher>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : TeachersUiState

    data class RemoteTeacherList(
        val teachers: List<Teacher>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : TeachersUiState
}

private data class TeachersViewModelState(
    val localTeachers: List<Teacher>? = null,
    val remoteTeachers: List<Teacher>? = null,
    val selectedName: String = "",
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toLocalUiState(): TeachersUiState.LocalTeacherList = TeachersUiState.LocalTeacherList(
        teachers = localTeachers ?: emptyList(),
        isLoading = isLoading,
        errorMessages = errorMessages
    )

    fun toSearchUiState(): TeachersUiState.RemoteTeacherList = TeachersUiState.RemoteTeacherList(
        teachers = remoteTeachers ?: emptyList(),
        isLoading = isLoading,
        errorMessages = errorMessages
    )
}

@HiltViewModel
class TeachersViewModel @Inject constructor(
    private val teachersUseCase: TeachersUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        TeachersViewModelState(
            isLoading = true
        )
    )

    val localUiState: StateFlow<TeachersUiState.LocalTeacherList> = viewModelState
        .map { it.toLocalUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toLocalUiState()
        )

    val searchUiState: StateFlow<TeachersUiState.RemoteTeacherList> = viewModelState
        .map { it.toSearchUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toSearchUiState()
        )

    val teacherNameSearchState = mutableStateOf("")

    fun getLocalTeachers() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = teachersUseCase.getLocalTeachers()
            viewModelState.update { state ->
                return@update state.copy(
                    localTeachers = result.successOr(emptyList()),
                    isLoading = false,
                )
            }
        }
    }

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

    fun saveAndPickTeacher(teacher: Teacher) {
        viewModelScope.launch {
            teachersUseCase.saveAndPickTeacher(teacher)
        }
    }
}