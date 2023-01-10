package dev.timatifey.posanie.ui.picker

import dev.timatifey.posanie.model.domain.Teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.model.successOr
import dev.timatifey.posanie.usecases.GroupsUseCase
import dev.timatifey.posanie.usecases.TeachersUseCase
import dev.timatifey.posanie.utils.ErrorMessage

data class LocalUiState(
    val levelsToGroups: Map<Int, GroupsLevel>,
    val teachers: List<Teacher>,
    val isLoading: Boolean,
    val errorMessages: List<ErrorMessage>
)

@HiltViewModel
class PickerViewModel @Inject constructor(
    private val groupsUseCase: GroupsUseCase,
    private val teachersUseCase: TeachersUseCase
) : ViewModel() {

    private data class ViewModelState(
        val localGroups: Map<Int, GroupsLevel>? = null,
        val localTeachers: List<Teacher>? = null,
        val commandsRunning: Int = 0,
        val errorMessages: List<ErrorMessage> = emptyList(),
    ) {

        fun toLocalUiState(): LocalUiState = LocalUiState(
            levelsToGroups = localGroups ?: emptyMap(),
            teachers = localTeachers ?: emptyList(),
            isLoading = commandsRunning > 0,
            errorMessages = errorMessages
        )
    }

    private val viewModelState = MutableStateFlow(
        ViewModelState(
            commandsRunning = 0
        )
    )

    val localUiState: StateFlow<LocalUiState> = viewModelState
        .map { it.toLocalUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toLocalUiState()
        )

    suspend fun getLocalGroups() {
        viewModelState.update { it.copy(commandsRunning = it.commandsRunning + 1) }

        val result = groupsUseCase.getLocalGroups()
        viewModelState.update { state ->
            return@update state.copy(
                localGroups = result.successOr(emptyMap()),
                commandsRunning = state.commandsRunning - 1,
            )
        }
    }

    suspend fun getLocalTeachers() {
        viewModelState.update { it.copy(commandsRunning = it.commandsRunning + 1) }

        val result = teachersUseCase.getLocalTeachers()
        viewModelState.update { state ->
            return@update state.copy(
                localTeachers = result.successOr(emptyList()),
                commandsRunning = state.commandsRunning - 1,
            )
        }
    }

    fun saveAndPickGroup(group: Group) {
        viewModelScope.launch {
            viewModelState.update { it.copy(commandsRunning = it.commandsRunning + 1) }

            groupsUseCase.saveAndPickGroup(group)
            getLocalGroups()
            teachersUseCase.pickTeacher(null)
            getLocalTeachers()

            viewModelState.update { it.copy(commandsRunning = it.commandsRunning - 1) }
        }
    }

    fun pickGroup(group: Group) {
        viewModelScope.launch {
            groupsUseCase.pickGroup(group)
            getLocalGroups()
            teachersUseCase.pickTeacher(null)
            getLocalTeachers()
        }
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            groupsUseCase.deleteGroup(group)
            getLocalGroups()
        }
    }

    fun saveAndPickTeacher(teacher: Teacher) {
        viewModelScope.launch {
            viewModelState.update { it.copy(commandsRunning = it.commandsRunning + 1) }

            groupsUseCase.pickGroup(null)
            getLocalGroups()
            teachersUseCase.saveAndPickTeacher(teacher)
            getLocalTeachers()

            viewModelState.update { it.copy(commandsRunning = it.commandsRunning - 1) }
        }
    }

    fun pickTeacher(teacher: Teacher) {
        viewModelScope.launch {
            groupsUseCase.pickGroup(null)
            getLocalGroups()
            teachersUseCase.pickTeacher(teacher)
            getLocalTeachers()
        }
    }

    fun deleteTeacher(teacher: Teacher) {
        viewModelScope.launch {
            teachersUseCase.deleteTeacher(teacher)
            getLocalTeachers()
        }
    }
}