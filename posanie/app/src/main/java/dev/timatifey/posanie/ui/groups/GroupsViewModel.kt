package dev.timatifey.posanie.ui.groups

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
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.successOr
import dev.timatifey.posanie.usecases.GroupsUseCase
import dev.timatifey.posanie.utils.ErrorMessage

sealed interface GroupsUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class GroupList(
        val groups: List<Group>,
        val selectedKind: Kind,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : GroupsUiState

}

private data class GroupsViewModelState(
    val groups: List<Group>? = null,
    val selectedKind: Kind,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): GroupsUiState = GroupsUiState.GroupList(
        groups = groups.orEmpty(),
        selectedKind = selectedKind,
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
            isLoading = true, selectedKind = Kind.BACHELOR
        )
    )

    val uiState: StateFlow<GroupsUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun getLocalGroups() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = groupsUseCase.getLocalGroups()
            viewModelState.update { state ->
                return@update state.copy(
                    groups = result.successOr(emptyList()),
                    isLoading = false,
                )
            }
        }
    }

    fun fetchGroupsBy(facultyId: Long, kindId: Long) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = groupsUseCase.fetchGroupsBy(facultyId, kindId)
            viewModelState.update { state ->
                when (result) {
                    is Result.Success -> {
                        return@update state.copy(
                            groups = result.data,
                            isLoading = false,
                            selectedKind = Kind.kindBy(id = kindId)
                        )
                    }
                    is Result.Error -> {
                        val errorMessages = state.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        return@update state.copy(
                            groups = emptyList(),
                            errorMessages = errorMessages,
                            isLoading = false,
                            selectedKind = Kind.kindBy(id = kindId)
                        )
                    }
                }
            }
        }
    }

    fun saveAndPickGroup(group: Group) {
        viewModelScope.launch {
            groupsUseCase.saveAndPickGroup(group)
        }
    }

}