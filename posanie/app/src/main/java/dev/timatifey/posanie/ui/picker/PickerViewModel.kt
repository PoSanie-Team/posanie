package dev.timatifey.posanie.ui.picker

import dev.timatifey.posanie.model.domain.Teacher

import androidx.compose.runtime.mutableStateOf
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
import java.util.*
import javax.inject.Inject

import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.model.successOr
import dev.timatifey.posanie.usecases.GroupsUseCase
import dev.timatifey.posanie.usecases.TeachersUseCase
import dev.timatifey.posanie.utils.ErrorMessage

sealed interface UiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class LocalList(
        val levelsToGroups: Map<Int, GroupsLevel>,
        val teachers: List<Teacher>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : UiState

    data class RemoteGroupList(
        val levelsToGroups: Map<Int, GroupsLevel>,
        val selectedKind: Kind,
        val selectedType: Type,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : UiState

    data class RemoteTeacherList(
        val teachers: List<Teacher>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : UiState
}

private data class ViewModelState(
    val localGroups: Map<Int, GroupsLevel>? = null,
    val remoteGroups: Map<Int, GroupsLevel>? = null,
    val localTeachers: List<Teacher>? = null,
    val remoteTeachers: List<Teacher>? = null,
    val filteredRemoteGroups: Map<Int, GroupsLevel>? = null,
    val selectedKind: Kind = Kind.DEFAULT,
    val selectedType: Type = Type.DEFAULT,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toLocalUiState(): UiState.LocalList = UiState.LocalList(
        levelsToGroups = localGroups ?: emptyMap(),
        teachers = localTeachers ?: emptyList(),
        isLoading = isLoading,
        errorMessages = errorMessages
    )

    fun toGroupSearchUiState(): UiState.RemoteGroupList = UiState.RemoteGroupList(
        levelsToGroups = filteredRemoteGroups ?: emptyMap(),
        selectedKind = selectedKind,
        selectedType = selectedType,
        isLoading = isLoading,
        errorMessages = errorMessages
    )

    fun toTeacherSearchUiState(): UiState.RemoteTeacherList = UiState.RemoteTeacherList(
        teachers = remoteTeachers ?: emptyList(),
        isLoading = isLoading,
        errorMessages = errorMessages
    )
}

@HiltViewModel
class PickerViewModel @Inject constructor(
    private val groupsUseCase: GroupsUseCase,
    private val teachersUseCase: TeachersUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        ViewModelState(
            isLoading = true
        )
    )

    val localUiState: StateFlow<UiState.LocalList> = viewModelState
        .map { it.toLocalUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toLocalUiState()
        )

    val groupSearchUiState: StateFlow<UiState.RemoteGroupList> = viewModelState
        .map { it.toGroupSearchUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toGroupSearchUiState()
        )

    val teacherSearchUiState: StateFlow<UiState.RemoteTeacherList> = viewModelState
        .map { it.toTeacherSearchUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toTeacherSearchUiState()
        )

    val courseSearchState = mutableStateOf("")
    val groupSearchState = mutableStateOf("")
    val teacherNameSearchState = mutableStateOf("")

    fun fetchGroupsBy(facultyId: Long) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = groupsUseCase.fetchGroupsBy(facultyId)
            viewModelState.update { state ->
                when (result) {
                    is Result.Success -> {
                        return@update state.copy(
                            remoteGroups = result.data,
                            filteredRemoteGroups = result.data
                        )
                    }
                    is Result.Error -> {
                        val errorMessages = state.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        return@update state.copy(
                            remoteGroups = emptyMap(),
                            filteredRemoteGroups = emptyMap(),
                            errorMessages = errorMessages
                        )
                    }
                }
            }
            filterGroups()
        }
    }

    fun selectFilters(kind: Kind, type: Type) {
        viewModelState.update { state ->
            return@update state.copy(
                selectedKind = kind,
                selectedType = type
            )
        }
    }

    fun filterGroups()  {
        val kind = viewModelState.value.selectedKind
        val type = viewModelState.value.selectedType
        val nameRegex = makeFilterRegex(
            groupType = type,
            course = courseSearchState.value,
            group = groupSearchState.value
        )
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            viewModelState.update { state ->
                val groups = state.remoteGroups
                val filteredGroups = mutableMapOf<Int, GroupsLevel>()
                for (level in groups?.keys ?: emptyList()) {
                    val groupsLevel = GroupsLevel(level)
                    for (group in groups?.get(level)?.getGroups() ?: emptyList()) {
                        val kindMatches = group.kindId == kind.id
                        val typeMatches = type == Type.ANY || group.typeId == type.id
                        if (nameRegex.matches(group.title) && kindMatches && typeMatches) {
                            groupsLevel.add(group)
                        }
                    }
                    if (groupsLevel.getGroups().isNotEmpty()) {
                        filteredGroups[level] = groupsLevel
                    }
                }
                return@update state.copy(
                    filteredRemoteGroups = filteredGroups,
                    isLoading = false
                )
            }
        }
    }

    private fun makeFilterRegex(groupType: Type, course: String, group: String): Regex {
        val result = if (groupType == Type.ANY) {
            """.{0,1}$course\d*\/$group.*"""
        } else {
            """${groupType.prefix}$course\d*\/$group.*"""
        }
        return Regex(result)
    }
    fun getLocalGroups() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = groupsUseCase.getLocalGroups()
            viewModelState.update { state ->
                return@update state.copy(
                    localGroups = result.successOr(emptyMap()),
                    isLoading = false,
                )
            }
        }
    }

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

    fun saveAndPickGroup(group: Group) {
        viewModelScope.launch {
            groupsUseCase.saveAndPickGroup(group)
            getLocalGroups()
            teachersUseCase.pickTeacher(null)
            getLocalTeachers()
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

    fun saveAndPickTeacher(teacher: Teacher) {
        viewModelScope.launch {
            groupsUseCase.pickGroup(null)
            getLocalGroups()
            teachersUseCase.saveAndPickTeacher(teacher)
            getLocalTeachers()
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
}