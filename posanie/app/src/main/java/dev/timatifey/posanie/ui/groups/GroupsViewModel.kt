package dev.timatifey.posanie.ui.groups

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import dev.timatifey.posanie.utils.ErrorMessage

sealed interface GroupsUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class LocalGroupList(
        val groups: Map<Int, GroupsLevel>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : GroupsUiState

    data class SearchGroupList(
        val groups: Map<Int, GroupsLevel>,
        val selectedKind: Kind,
        val selectedType: Type,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : GroupsUiState
}

private data class GroupsViewModelState(
    val localGroups: Map<Int, GroupsLevel>? = null,
    val remoteGroups: Map<Int, GroupsLevel>? = null,
    val filteredRemoteGroups: Map<Int, GroupsLevel>? = null,
    val selectedKind: Kind = Kind.DEFAULT,
    val selectedType: Type = Type.DEFAULT,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toLocalUiState(): GroupsUiState.LocalGroupList = GroupsUiState.LocalGroupList(
        groups = localGroups ?: emptyMap(),
        isLoading = isLoading,
        errorMessages = errorMessages
    )

    fun toSearchUiState(): GroupsUiState.SearchGroupList = GroupsUiState.SearchGroupList(
        groups = filteredRemoteGroups ?: emptyMap(),
        selectedKind = selectedKind,
        selectedType = selectedType,
        isLoading = isLoading,
        errorMessages = errorMessages
    )
}

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupsUseCase: GroupsUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        GroupsViewModelState(
            isLoading = true
        )
    )

    val localUiState: StateFlow<GroupsUiState.LocalGroupList> = viewModelState
        .map { it.toLocalUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toLocalUiState()
        )

    val searchUiState: StateFlow<GroupsUiState.SearchGroupList> = viewModelState
        .map { it.toSearchUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toSearchUiState()
        )

    val courseSearchState = mutableStateOf("")
    val groupSearchState = mutableStateOf("")

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

    fun select(kind: Kind, type: Type) {
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

    fun saveAndPickGroup(group: Group) {
        viewModelScope.launch {
            groupsUseCase.saveAndPickGroup(group)
        }
    }

}