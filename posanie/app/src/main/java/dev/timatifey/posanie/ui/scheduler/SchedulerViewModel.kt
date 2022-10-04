package dev.timatifey.posanie.ui.scheduler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

enum class WeekDay(val shortName: String) {
    MONDAY("MO"),
    TUESDAY("TU"),
    WEDNESDAY("WE"),
    THURSDAY("TH"),
    FRIDAY("FR"),
    SATURDAY("SA");

    companion object {
        fun getByOrdinal(order: Int): WeekDay {
            when(order) {
                2 -> return MONDAY
                3 -> return TUESDAY
                4 -> return WEDNESDAY
                5 -> return THURSDAY
                6 -> return FRIDAY
                7 -> return SATURDAY
            }
            throw IllegalArgumentException("Illegal ordinal")
        }
    }
}

sealed interface SchedulerUiState {

    val mondayDate: Calendar
    val selectedDate: Calendar
    val selectedDay: WeekDay
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoGroup(
        override val mondayDate: Calendar,
        override val selectedDate: Calendar,
        override val selectedDay: WeekDay,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : SchedulerUiState

    data class HasGroup(
        val group: Group,
        override val mondayDate: Calendar,
        override val selectedDate: Calendar,
        override val selectedDay: WeekDay,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : SchedulerUiState
}

private data class SchedulerViewModelState(
    val group: Group? = null,
    val mondayDate: Calendar = Calendar.getInstance(),
    val selectedDate: Calendar = Calendar.getInstance(),
    val selectedDay: WeekDay = WeekDay.getByOrdinal(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)),
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): SchedulerUiState =
        if (group == null) {
            SchedulerUiState.NoGroup(
                mondayDate = mondayDate,
                selectedDate = selectedDate,
                selectedDay = selectedDay,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            SchedulerUiState.HasGroup(
                group = group,
                mondayDate = mondayDate,
                selectedDate = selectedDate,
                selectedDay = selectedDay,
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

    fun setMonday(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            viewModelState.update {
                val newWeekMonday = Calendar.getInstance()
                newWeekMonday.set(year, month, day)
                return@update it.copy(
                    mondayDate = newWeekMonday,
                    selectedDate = newWeekMonday,
                    selectedDay = WeekDay.MONDAY
                )
            }
        }
    }

    fun selectWeekDay(weekDay: WeekDay) {
        viewModelScope.launch {
            viewModelState.update {
                val newSelectedDay = Calendar.getInstance()
                val year = it.mondayDate.get(Calendar.YEAR)
                val month = it.mondayDate.get(Calendar.MONTH)
                val day = it.mondayDate.get(Calendar.DAY_OF_MONTH) + weekDay.ordinal
                newSelectedDay.set(year, month, day)
                return@update it.copy(selectedDate = newSelectedDay, selectedDay = weekDay)
            }
        }
    }
}