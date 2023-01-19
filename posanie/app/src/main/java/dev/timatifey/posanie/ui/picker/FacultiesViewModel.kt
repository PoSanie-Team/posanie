package dev.timatifey.posanie.ui.picker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel

import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.model.domain.Kind
import dev.timatifey.posanie.model.domain.Type
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

data class FacultiesUiState(
    val faculties: List<Faculty>,
    val isLoading: Boolean,
    val errorMessages: List<ErrorMessage>,
)

@HiltViewModel
class FacultiesViewModel @Inject constructor(
    private val facultiesUseCase: FacultiesUseCase
) : ViewModel() {

    private data class FacultiesViewModelState(
        val faculties: List<Faculty>? = null,
        val filteredFaculties: List<Faculty>? = null,
        val isLoading: Boolean = false,
        val errorMessages: List<ErrorMessage> = emptyList(),
    ) {
        fun toUiState(): FacultiesUiState =
            FacultiesUiState(
                faculties = filteredFaculties.orEmpty(),
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
    }

    private val viewModelState = MutableStateFlow(FacultiesViewModelState(isLoading = true))
    val searchState = mutableStateOf(SearchState.NOT_STARTED)
    val searchTextState = mutableStateOf("")

    val uiState: StateFlow<FacultiesUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        fetchFaculties()
    }

    fun fetchFaculties() {
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
                            errorMessages = emptyList(),
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        val errorMessages = listOf(ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.no_faculties_error_message
                        ))
                        state.copy(
                            faculties = null,
                            errorMessages = errorMessages,
                            isLoading = false
                        )
                    }
                }
            }
            filterFaculties()
        }
    }

    fun filterFaculties()  {
        val titleRegex = makeFilterRegex(searchTextState.value)
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            viewModelState.update { state ->
                val faculties = state.faculties
                val filteredFaculties = mutableListOf<Faculty>()
                for (faculty in faculties ?: emptyList()) {
                    if (titleRegex.matches(faculty.title.lowercase())) {
                        filteredFaculties.add(faculty)
                    }
                }
                return@update state.copy(
                    filteredFaculties = filteredFaculties,
                    isLoading = false
                )
            }
        }
    }

    private fun makeFilterRegex(search: String): Regex {
        val result = """.*${search.lowercase().trim()}.*"""
        return Regex(result)
    }

    fun getFaculty(id: Long): Faculty? {
        for (faculty in viewModelState.value.faculties ?: emptyList()) {
            if (faculty.id == id) return faculty
        }
        return null
    }
}