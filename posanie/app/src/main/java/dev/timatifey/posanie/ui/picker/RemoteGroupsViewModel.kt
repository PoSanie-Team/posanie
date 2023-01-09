package dev.timatifey.posanie.ui.picker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Kind
import dev.timatifey.posanie.model.domain.Type
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.usecases.GroupsUseCase
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class RemoteGroupsUiState(
    val levelsToGroups: Map<Int, GroupsLevel>,
    val selectedKind: Kind,
    val selectedType: Type,
    val isLoading: Boolean,
    val errorMessages: List<ErrorMessage>
)

@HiltViewModel
class RemoteGroupsViewModel @Inject constructor(
    private val groupsUseCase: GroupsUseCase
) : ViewModel() {

    private data class ViewModelState(
        val remoteGroups: Map<Int, GroupsLevel>? = null,
        val filteredRemoteGroups: Map<Int, GroupsLevel>? = null,
        val selectedKind: Kind = Kind.DEFAULT,
        val selectedType: Type = Type.DEFAULT,
        val isLoading: Boolean = false,
        val errorMessages: List<ErrorMessage> = emptyList()
    ) {
        fun toUiState(): RemoteGroupsUiState = RemoteGroupsUiState(
            levelsToGroups = filteredRemoteGroups ?: emptyMap(),
            selectedKind = selectedKind,
            selectedType = selectedType,
            isLoading = isLoading,
            errorMessages = errorMessages
        )
    }

    private val viewModelState = MutableStateFlow(
        ViewModelState(
            isLoading = true
        )
    )

    val uiState: StateFlow<RemoteGroupsUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )
    val courseSearchState = mutableStateOf("")
    val groupSearchState = mutableStateOf("")

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
}