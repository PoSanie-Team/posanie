package dev.timatifey.posanie.ui.scheduler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.Lesson
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.model.successOr
import dev.timatifey.posanie.usecases.GroupsUseCase
import dev.timatifey.posanie.usecases.LessonsUseCase
import dev.timatifey.posanie.usecases.TeachersUseCase
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.IllegalArgumentException

enum class WeekWorkDay(val shortName: String) {
    MONDAY("MO"),
    TUESDAY("TU"),
    WEDNESDAY("WE"),
    THURSDAY("TH"),
    FRIDAY("FR"),
    SATURDAY("SA");

    companion object {
        fun getByOrdinal(ordinal: Int): WeekWorkDay {
            when(ordinal) {
                2 -> return MONDAY
                3 -> return TUESDAY
                4 -> return WEDNESDAY
                5 -> return THURSDAY
                6 -> return FRIDAY
                7 -> return SATURDAY
                1 -> return SATURDAY
            }
            throw IllegalArgumentException("Illegal ordinal")
        }
    }
}

enum class Month(val fullName: String) {
    JANUARY("January"),
    FEBRUARY("February"),
    MARCH("March"),
    APRIL("April"),
    MAY("May"),
    JUNE("June"),
    JULY("July"),
    AUGUST("August"),
    SEPTEMBER("September"),
    OCTOBER("October"),
    NOVEMBER("November"),
    DECEMBER("December");

    companion object {
        fun getByOrdinal(ordinal: Int): Month {
            when(ordinal) {
                0 -> return JANUARY
                1 -> return FEBRUARY
                2 -> return MARCH
                3 -> return APRIL
                4 -> return MAY
                5 -> return JUNE
                6 -> return JULY
                7 -> return AUGUST
                8 -> return SEPTEMBER
                9 -> return OCTOBER
                10 -> return NOVEMBER
                11 -> return DECEMBER
            }
            throw IllegalArgumentException("Illegal ordinal")
        }

        fun getDaysCount(year: Int, month: Month): Int {
            return when (month) {
                JANUARY -> 31
                FEBRUARY -> if (year % 4 == 0) 29 else 28
                MARCH -> 30
                APRIL -> 31
                MAY -> 30
                JUNE -> 31
                JULY -> 31
                AUGUST -> 31
                SEPTEMBER -> 30
                OCTOBER -> 31
                NOVEMBER -> 30
                DECEMBER -> 31
            }
        }
    }
}

sealed interface SchedulerUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class UiState(
        val hasSchedule: Boolean = false,
        val weekIsOdd: Boolean = false,
        val lessonsToDays: Map<WeekWorkDay, List<Lesson>>,
        val mondayDate: Calendar,
        val selectedDate: Calendar,
        val selectedDay: WeekWorkDay,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : SchedulerUiState
}

private data class SchedulerViewModelState(
    val hasSchedule: Boolean = false,
    val weekIsOdd: Boolean = false,
    val lessonsToDays: Map<WeekWorkDay, List<Lesson>>? = null,
    val selectedLessons: List<Lesson>? = null,
    val mondayDate: Calendar,
    val selectedDate: Calendar,
    val selectedDay: WeekWorkDay,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    companion object {
        fun getInstance(): SchedulerViewModelState {
            val todayDate: Calendar = Calendar.getInstance()

            val today = WeekWorkDay.getByOrdinal(todayDate.get(Calendar.DAY_OF_WEEK))
            val monthOnCalendar = Month.getByOrdinal(todayDate.get(Calendar.MONTH))

            val todayYear = todayDate.get(Calendar.YEAR)
            val todayMonth = todayDate.get(Calendar.MONTH)

            val todayDay = todayDate.get(Calendar.DAY_OF_MONTH)
            val correctedTodayDay = if (isSunday(todayDate)) todayDay - 1 else todayDay

            val mondayDay = correctedTodayDay - today.ordinal
            val mondayDate = Calendar.getInstance()

            mondayDate.set(todayYear, todayMonth, mondayDay)

            return SchedulerViewModelState(
                mondayDate = mondayDate,
                selectedDate = todayDate,
                selectedDay = today,
                isLoading = true,
                hasSchedule = false
            )
        }

        fun isSunday(date: Calendar): Boolean {
            return date.get(Calendar.DAY_OF_WEEK) == 1
        }
    }

    fun toUiState(): SchedulerUiState.UiState =
        SchedulerUiState.UiState(
            hasSchedule = hasSchedule,
            weekIsOdd = weekIsOdd,
            lessonsToDays = lessonsToDays ?: emptyMap(),
            mondayDate = mondayDate,
            selectedDate = selectedDate,
            selectedDay = selectedDay,
            isLoading = isLoading,
            errorMessages = errorMessages,
        )
}

@HiltViewModel
class SchedulerViewModel @Inject constructor(
    private val lessonsUseCase: LessonsUseCase,
    private val groupsUseCase: GroupsUseCase,
    private val teachersUseCase: TeachersUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SchedulerViewModelState.getInstance())

    val uiState: StateFlow<SchedulerUiState.UiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun selectNextWeekDay() {
        val newDay = getNextWeekDay()
        if (newDay == WeekWorkDay.MONDAY) {
            setNextMonday()
        }
        selectWeekDay(newDay)
    }

    private fun getNextWeekDay(): WeekWorkDay {
        val result = when (viewModelState.value.selectedDay) {
            WeekWorkDay.MONDAY -> WeekWorkDay.TUESDAY
            WeekWorkDay.TUESDAY -> WeekWorkDay.WEDNESDAY
            WeekWorkDay.WEDNESDAY -> WeekWorkDay.THURSDAY
            WeekWorkDay.THURSDAY -> WeekWorkDay.FRIDAY
            WeekWorkDay.FRIDAY -> WeekWorkDay.SATURDAY
            else -> WeekWorkDay.MONDAY
        }
        return result
    }

    fun selectPreviousWeekDay() {
        val newDay = getPreviousWeekDay()
        if (newDay == WeekWorkDay.SATURDAY) {
            setPreviousMonday()
        }
        selectWeekDay(newDay)
    }

    private fun getPreviousWeekDay(): WeekWorkDay {
        val result = when (viewModelState.value.selectedDay) {
            WeekWorkDay.SATURDAY -> WeekWorkDay.FRIDAY
            WeekWorkDay.FRIDAY -> WeekWorkDay.THURSDAY
            WeekWorkDay.THURSDAY -> WeekWorkDay.WEDNESDAY
            WeekWorkDay.WEDNESDAY -> WeekWorkDay.TUESDAY
            WeekWorkDay.TUESDAY -> WeekWorkDay.MONDAY
            else -> WeekWorkDay.SATURDAY
        }

        return result
    }

    fun selectWeekDay(weekDay: WeekWorkDay) {
        viewModelScope.launch {
            viewModelState.update {
                val newSelectedDate = Calendar.getInstance()
                val year = it.mondayDate.get(Calendar.YEAR)
                val month = it.mondayDate.get(Calendar.MONTH)
                val day = it.mondayDate.get(Calendar.DAY_OF_MONTH) + weekDay.ordinal
                newSelectedDate.set(year, month, day)
                return@update it.copy(
                    selectedDate = newSelectedDate,
                    selectedDay = weekDay,
                    selectedLessons = it.lessonsToDays?.get(weekDay)
                )
            }
        }
    }

    fun selectDate(newDate: Calendar) {
        val dayOrdinal = newDate.get(Calendar.DAY_OF_WEEK)
        val newWeekDay = WeekWorkDay.getByOrdinal(dayOrdinal)

        val newYear = newDate.get(Calendar.YEAR)
        val newMonth = newDate.get(Calendar.MONTH)

        val newDay = newDate.get(Calendar.DAY_OF_MONTH)
        val correctedNewDay = if (SchedulerViewModelState.isSunday(newDate)) newDay - 1 else newDay

        val mondayDay = correctedNewDay - newWeekDay.ordinal

        setMonday(newYear, newMonth, mondayDay)
        selectWeekDay(newWeekDay)
    }

    fun setNextMonday() {
        val d = viewModelState.value.mondayDate.get(Calendar.DAY_OF_MONTH)
        val m = viewModelState.value.mondayDate.get(Calendar.MONTH)
        val y = viewModelState.value.mondayDate.get(Calendar.YEAR)
        setMonday(y, m, d + 7)
    }

    fun setPreviousMonday() {
        val d = viewModelState.value.mondayDate.get(Calendar.DAY_OF_MONTH)
        val m = viewModelState.value.mondayDate.get(Calendar.MONTH)
        val y = viewModelState.value.mondayDate.get(Calendar.YEAR)
        setMonday(y, m, d - 7)
    }

    private fun setMonday(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            viewModelState.update {
                val newWeekMonday = Calendar.getInstance()
                newWeekMonday.set(year, month, day)
                check(newWeekMonday.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    "Provided day is not monday."
                }
                return@update it.copy(
                    mondayDate = newWeekMonday,
                    selectedDate = newWeekMonday,
                    selectedLessons = emptyList()
                )
            }
            fetchLessons()
        }
    }

    fun fetchLessons() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val group: Group? = groupsUseCase.getPickedGroup().successOr(null)
            val teacher: Teacher? = teachersUseCase.getPickedTeacher().successOr(null)

            val day = uiState.value.mondayDate.get(Calendar.DAY_OF_MONTH)
            val month = uiState.value.mondayDate.get(Calendar.MONTH) + 1
            val year = uiState.value.mondayDate.get(Calendar.YEAR)
            val mondayDateString = "$year-$month-$day"
            val lessonsResult: dev.timatifey.posanie.model.Result<Map<WeekWorkDay, List<Lesson>>>
            val isOddResult: dev.timatifey.posanie.model.Result<Boolean>
            if (group != null) {
                lessonsResult = lessonsUseCase.fetchLessonsByGroupId(group.id, mondayDateString)
                isOddResult = lessonsUseCase.fetchWeekOddnessByGroupId(group.id, mondayDateString)
            } else if (teacher != null) {
                lessonsResult = lessonsUseCase.fetchLessonsByTeacherId(teacher.id, mondayDateString)
                isOddResult = lessonsUseCase.fetchWeekOddnessByGroupId(teacher.id, mondayDateString)
            } else {
                viewModelState.update { it.copy(isLoading = false, hasSchedule = false) }
                return@launch
            }
            val newLessonToDays = lessonsResult.successOr(emptyMap())
            val weekIsOdd = isOddResult.successOr(false)
            viewModelState.update { state ->
                return@update state.copy(
                    hasSchedule = true,
                    weekIsOdd = weekIsOdd,
                    lessonsToDays = newLessonToDays,
                    selectedLessons = newLessonToDays[state.selectedDay],
                    isLoading = false,
                )
            }
        }
    }
}