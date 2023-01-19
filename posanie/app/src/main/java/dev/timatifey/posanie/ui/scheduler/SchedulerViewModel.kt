package dev.timatifey.posanie.ui.scheduler

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.timatifey.posanie.R
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
import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.ui.ConnectionState

enum class WeekDay(@StringRes val shortNameId: Int) {
    MONDAY(R.string.monday_short_name),
    TUESDAY(R.string.tuesday_short_name),
    WEDNESDAY(R.string.wednesday_short_name),
    THURSDAY(R.string.thursday_short_name),
    FRIDAY(R.string.friday_short_name),
    SATURDAY(R.string.saturday_short_name),
    SUNDAY(R.string.sunday_short_name);

    companion object {
        fun getDayByOrdinal(ordinal: Int): WeekDay {
            when(ordinal) {
                2 -> return MONDAY
                3 -> return TUESDAY
                4 -> return WEDNESDAY
                5 -> return THURSDAY
                6 -> return FRIDAY
                7 -> return SATURDAY
                1 -> return SUNDAY
            }
            throw IllegalArgumentException("Illegal ordinal")
        }

        fun getWorkDayByOrdinal(ordinal: Int): WeekDay {
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

enum class Month(@StringRes val fullNameId: Int) {
    JANUARY(R.string.january_full_name),
    FEBRUARY(R.string.february_full_name),
    MARCH(R.string.march_full_name),
    APRIL(R.string.april_full_name),
    MAY(R.string.may_full_name),
    JUNE(R.string.june_full_name),
    JULY(R.string.july_full_name),
    AUGUST(R.string.august_full_name),
    SEPTEMBER(R.string.september_full_name),
    OCTOBER(R.string.october_full_name),
    NOVEMBER(R.string.november_full_name),
    DECEMBER(R.string.december_full_name);

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

data class SchedulerUiState(
    val hasSchedule: Boolean = false,
    val weekIsOdd: Boolean = false,
    val lessonsToDays: Map<WeekDay, List<Lesson>>,
    val mondayDate: Calendar,
    val selectedDate: Calendar,
    val selectedDay: WeekDay,
    val isLoading: Boolean,
    val errorMessages: List<ErrorMessage>,
)

private data class SchedulerViewModelState(
    val hasSchedule: Boolean = false,
    val weekIsOdd: Boolean = false,
    val lessonsToDays: Map<WeekDay, List<Lesson>>? = null,
    val mondayDate: Calendar,
    val selectedDate: Calendar,
    val selectedDay: WeekDay,
    val connectionState: ConnectionState,
    val isLoading: Int = 0,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    companion object {
        fun getInstance(): SchedulerViewModelState {
            val todayDate: Calendar = Calendar.getInstance()

            val today = WeekDay.getWorkDayByOrdinal(todayDate.get(Calendar.DAY_OF_WEEK))

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
                connectionState = ConnectionState.AVAILABLE,
                isLoading = 0,
                hasSchedule = false
            )
        }

        fun isSunday(date: Calendar): Boolean {
            return date.get(Calendar.DAY_OF_WEEK) == 1
        }
    }

    fun toUiState(): SchedulerUiState =
        SchedulerUiState(
            hasSchedule = hasSchedule,
            weekIsOdd = weekIsOdd,
            lessonsToDays = lessonsToDays ?: emptyMap(),
            mondayDate = mondayDate,
            selectedDate = selectedDate,
            selectedDay = selectedDay,
            isLoading = isLoading > 0,
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

    val uiState: StateFlow<SchedulerUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun selectNextWeekDay() {
        val newDay = getNextWeekDay()
        if (newDay == WeekDay.MONDAY) {
            setNextMonday()
        }
        selectWeekDay(newDay)
    }

    private fun getNextWeekDay(): WeekDay {
        val result = when (viewModelState.value.selectedDay) {
            WeekDay.MONDAY -> WeekDay.TUESDAY
            WeekDay.TUESDAY -> WeekDay.WEDNESDAY
            WeekDay.WEDNESDAY -> WeekDay.THURSDAY
            WeekDay.THURSDAY -> WeekDay.FRIDAY
            WeekDay.FRIDAY -> WeekDay.SATURDAY
            else -> WeekDay.MONDAY
        }
        return result
    }

    fun selectPreviousWeekDay() {
        val newDay = getPreviousWeekDay()
        if (newDay == WeekDay.SATURDAY) {
            setPreviousMonday()
        }
        selectWeekDay(newDay)
    }

    private fun getPreviousWeekDay(): WeekDay {
        val result = when (viewModelState.value.selectedDay) {
            WeekDay.SATURDAY -> WeekDay.FRIDAY
            WeekDay.FRIDAY -> WeekDay.THURSDAY
            WeekDay.THURSDAY -> WeekDay.WEDNESDAY
            WeekDay.WEDNESDAY -> WeekDay.TUESDAY
            WeekDay.TUESDAY -> WeekDay.MONDAY
            else -> WeekDay.SATURDAY
        }

        return result
    }

    fun selectWeekDay(weekDay: WeekDay) {
        viewModelScope.launch {
            viewModelState.update {
                val newSelectedDate = Calendar.getInstance()
                val year = it.mondayDate.get(Calendar.YEAR)
                val month = it.mondayDate.get(Calendar.MONTH)
                val day = it.mondayDate.get(Calendar.DAY_OF_MONTH) + weekDay.ordinal
                newSelectedDate.set(year, month, day)
                return@update it.copy(
                    selectedDate = newSelectedDate,
                    selectedDay = weekDay
                )
            }
        }
    }

    fun selectDate(newDate: Calendar) {
        val dayOrdinal = newDate.get(Calendar.DAY_OF_WEEK)
        val newWeekDay = WeekDay.getWorkDayByOrdinal(dayOrdinal)

        val newYear = newDate.get(Calendar.YEAR)
        val newMonth = newDate.get(Calendar.MONTH)

        val newDay = newDate.get(Calendar.DAY_OF_MONTH)
        val correctedNewDay = if (SchedulerViewModelState.isSunday(newDate)) newDay - 1 else newDay

        val mondayDay = correctedNewDay - newWeekDay.ordinal

        setMonday(newYear, newMonth, mondayDay)
        selectWeekDay(newWeekDay)
    }

    fun setNextMonday() {
        if (viewModelState.value.connectionState == ConnectionState.UNAVAILABLE) return
        val d = viewModelState.value.mondayDate.get(Calendar.DAY_OF_MONTH)
        val m = viewModelState.value.mondayDate.get(Calendar.MONTH)
        val y = viewModelState.value.mondayDate.get(Calendar.YEAR)
        setMonday(y, m, d + 7)
    }

    fun setPreviousMonday() {
        if (viewModelState.value.connectionState == ConnectionState.UNAVAILABLE) return
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
                    selectedDate = newWeekMonday
                )
            }
            fetchLessons()
        }
    }

    fun updateConnectionState(connectionState: ConnectionState) {
        viewModelState.update { state -> state.copy(connectionState = connectionState) }
        if (connectionState == ConnectionState.AVAILABLE) fetchLessons()
    }

    private suspend fun getLessons() {
        viewModelState.update { it.copy(isLoading = it.isLoading + 1) }

        val group: Group? = groupsUseCase.getPickedGroup().successOr(null)
        val teacher: Teacher? = teachersUseCase.getPickedTeacher().successOr(null)

        val lessonsResult: Result<Map<WeekDay, List<Lesson>>>
        val isOddResult: Result<Boolean>
        val mondayDateResult: Result<Calendar>

        if (group != null) {
            lessonsResult = lessonsUseCase.getLessonsByGroupId(group.id)
            isOddResult = lessonsUseCase.getGroupSchedulerWeekOddness(group.id)
            mondayDateResult = lessonsUseCase.getGroupSchedulerWeekMonday(group.id)
        } else if (teacher != null) {
            lessonsResult = lessonsUseCase.getLessonsByTeacherId(teacher.id)
            isOddResult = lessonsUseCase.getTeacherSchedulerWeekOddness(teacher.id)
            mondayDateResult = lessonsUseCase.getTeacherSchedulerWeekMonday(teacher.id)
        } else {
            viewModelState.update { it.copy(isLoading = it.isLoading - 1, hasSchedule = false) }
            return
        }
        val newLessonToDays = lessonsResult.successOr(emptyMap())
        val weekIsOdd = isOddResult.successOr(false)
        val mondayDate = mondayDateResult.successOr(viewModelState.value.mondayDate)
        val errorMessages =
            if (lessonsResult is Result.Error) listOf(exceptionToErrorMessage(lessonsResult.exception))
            else emptyList()

        val day = mondayDate.get(Calendar.DAY_OF_MONTH) + viewModelState.value.selectedDay.ordinal
        val month = mondayDate.get(Calendar.MONTH)
        val year = mondayDate.get(Calendar.YEAR)
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, day)

        viewModelState.update { state ->
            return@update state.copy(
                hasSchedule = true,
                weekIsOdd = weekIsOdd,
                mondayDate = mondayDate,
                selectedDate = selectedDate,
                lessonsToDays = newLessonToDays,
                errorMessages = errorMessages,
                isLoading = state.isLoading - 1,
            )
        }
    }

    private fun exceptionToErrorMessage(exception: Exception): ErrorMessage {
        return ErrorMessage(0, androidx.compose.ui.R.string.default_error_message)
    }

    fun fetchLessons() {
        viewModelState.update { it.copy(isLoading = it.isLoading + 1) }

        viewModelScope.launch {
            val group: Group? = groupsUseCase.getPickedGroup().successOr(null)
            val teacher: Teacher? = teachersUseCase.getPickedTeacher().successOr(null)

            val lessonsResult: Result<Map<WeekDay, List<Lesson>>>
            val isOddResult: Result<Boolean>
            val mondayDateString = convertDateToString(viewModelState.value.mondayDate)

            if (group != null) {
                lessonsResult = lessonsUseCase.fetchLessonsByGroupId(group.id, mondayDateString)
                isOddResult = lessonsUseCase.fetchWeekOddnessByGroupId(group.id, mondayDateString)
            } else if (teacher != null) {
                lessonsResult = lessonsUseCase.fetchLessonsByTeacherId(teacher.id, mondayDateString)
                isOddResult = lessonsUseCase.fetchWeekOddnessByTeacherId(teacher.id, mondayDateString)
            } else {
                viewModelState.update { it.copy(isLoading = it.isLoading - 1, hasSchedule = false) }
                return@launch
            }

            if (lessonsResult is Result.Success && isOddResult is Result.Success) {
                val newLessonToDays = lessonsResult.data
                val weekIsOdd = isOddResult.data
                viewModelState.update { state ->
                    return@update state.copy(
                        hasSchedule = true,
                        weekIsOdd = weekIsOdd,
                        lessonsToDays = newLessonToDays,
                        errorMessages = emptyList(),
                        isLoading = state.isLoading - 1
                    )
                }
                saveResults(group, teacher, isOddResult, lessonsResult)
            } else {
                getLessons()
                viewModelState.update { state ->
                    return@update state.copy(
                        isLoading = state.isLoading - 1
                    )
                }
            }
        }
    }

    private suspend fun saveResults(
        group: Group?,
        teacher: Teacher?,
        isOddResult: Result<Boolean>,
        lessonsResult: Result<Map<WeekDay, List<Lesson>>>
    ) {
        if (group != null) {
            lessonsUseCase.saveGroupLessons(
                groupId = group.id,
                mondayDate = viewModelState.value.mondayDate,
                weekIsOdd = isOddResult.successOr(false),
                lessonsToWeekDays = lessonsResult.successOr(emptyMap())
            )
        }
        if (teacher != null) {
            lessonsUseCase.saveTeacherLessons(
                teacherId = teacher.id,
                mondayDate = viewModelState.value.mondayDate,
                weekIsOdd = isOddResult.successOr(false),
                lessonsToWeekDays = lessonsResult.successOr(emptyMap())
            )
        }
    }

    private fun convertDateToString(date: Calendar): String {
        val day = date.get(Calendar.DAY_OF_MONTH)
        val month = fromCalendarMonthToApiMonth(date.get(Calendar.MONTH))
        val year = date.get(Calendar.YEAR)
        return "$year-$month-$day"
    }

    private fun fromCalendarMonthToApiMonth(month: Int) = month + 1
}