package dev.timatifey.posanie.ui.scheduler

import android.content.Context
import android.os.LocaleList
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.Lesson
import dev.timatifey.posanie.ui.ConnectionState
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(
    context: Context,
    schedulerViewModel: SchedulerViewModel,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit
) {
    val schedulerUiState = schedulerViewModel.uiState.collectAsState().value

    val lessonsToDays = schedulerUiState.lessonsToDays

    val weekDayListState = rememberLazyListState()
    val currentWeekDayOrdinal = schedulerUiState.selectedDay.ordinal
    val coroutineScope = rememberCoroutineScope()

    val calendarVisibilityState = remember { mutableStateOf(false) }

    LaunchedEffect(schedulerUiState.mondayDate) {
        weekDayListState.scrollToItem(currentWeekDayOrdinal)
    }

    coroutineScope.launch {
        weekDayListState.animateScrollToItem(currentWeekDayOrdinal)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                SchedulerBar(
                    schedulerViewModel = schedulerViewModel,
                    schedulerUiState = schedulerUiState,
                    openCalendar = { calendarVisibilityState.value = true },
                    showCannotLoadWeekToast = {
                        NoInternetConnectionToast.show(context, R.string.cannot_load_new_week_message)
                    }
                )
            }
        ) { paddingValues ->
            val weekState = WeekState(
                dayListState = weekDayListState,
                errorMessages = schedulerUiState.errorMessages,
                isLoading = schedulerUiState.isLoading,
                hasSchedule = schedulerUiState.hasSchedule
            )
            val weekScroller = createWeekScroller(
                context = context,
                schedulerViewModel = schedulerViewModel,
                coroutineScope = coroutineScope,
                weekDayListState = weekDayListState
            )
            ScrollableWeek(
                modifier = Modifier.padding(paddingValues),
                state = weekState,
                lessonsToDays = lessonsToDays,
                weekScroller = weekScroller,
                fetchLessons = {
                    schedulerViewModel.fetchLessons()
                    if (schedulerUiState.connectionState == ConnectionState.UNAVAILABLE) {
                        NoInternetConnectionToast.show(context, R.string.cannot_update_schedule_message)
                    }
                }
            )
        }

        LaunchedEffect(true) {
            createPopup(calendarVisibilityState) {
                CalendarView(
                    schedulerViewModel = schedulerViewModel,
                    close = { calendarVisibilityState.value = false }
                )
            }
        }
    }
}

@Composable
fun ScrollableWeek(
    modifier: Modifier = Modifier,
    state: WeekState,
    lessonsToDays: Map<WeekDay, List<Lesson>>,
    weekScroller: LazyListScroller,
    fetchLessons: () -> Unit
) {
    Box(modifier = modifier
        .fillMaxSize()
        .pointerInput(true) {
            this.handleSwipe(
                minSwipeLength = 50f,
                scrollToNextItem = weekScroller.scrollToNextItem,
                scrollToPreviousItem = weekScroller.scrollToPreviousItem,
                scrollToCurrentItem = weekScroller.scrollToCurrentItem,
                scrollBy = weekScroller.scrollBy
            )
        }
    ) {
        RefreshableWeek(
            state = state,
            lessonsToDays = lessonsToDays,
            fetchLessons = fetchLessons
        )
    }
}

class WeekState(
    val dayListState: LazyListState,
    val errorMessages: List<ErrorMessage>,
    val isLoading: Boolean,
    val hasSchedule: Boolean
)

class LazyListScroller(
    val scrollToNextItem: () -> Unit,
    val scrollToPreviousItem: () -> Unit,
    val scrollToCurrentItem: () -> Unit,
    val scrollBy: (Float) -> Unit
)

fun createWeekScroller(
    context: Context,
    schedulerViewModel: SchedulerViewModel,
    coroutineScope: CoroutineScope,
    weekDayListState: LazyListState
): LazyListScroller {
    return LazyListScroller(
        scrollToNextItem = {
            val oldDay = schedulerViewModel.uiState.value.selectedDay
            schedulerViewModel.selectNextWeekDay()
            val newDay = schedulerViewModel.uiState.value.selectedDay
            val connectionState = schedulerViewModel.uiState.value.connectionState
            showConnectionToastOnWeekChange(
                context = context,
                oldDay = oldDay,
                newDay = newDay,
                connectionState = connectionState
            )
        },
        scrollToPreviousItem = {
            val oldDay = schedulerViewModel.uiState.value.selectedDay
            schedulerViewModel.selectPreviousWeekDay()
            val newDay = schedulerViewModel.uiState.value.selectedDay
            val connectionState = schedulerViewModel.uiState.value.connectionState
            showConnectionToastOnWeekChange(
                context = context,
                oldDay = oldDay,
                newDay = newDay,
                connectionState = connectionState
            )
        },
        scrollToCurrentItem = {
            coroutineScope.launch {
                weekDayListState.animateScrollToItem(schedulerViewModel.uiState.value.selectedDay.ordinal)
            }
        },
        scrollBy = { x ->
            coroutineScope.launch {
                weekDayListState.scrollBy(x)
            }
        }
    )
}

@Composable
fun RefreshableWeek(
    state: WeekState,
    lessonsToDays: Map<WeekDay, List<Lesson>>,
    fetchLessons: () -> Unit
) {
    SwipeRefresh(
        modifier = Modifier
            .fillMaxSize(),
        state = rememberSwipeRefreshState(state.isLoading),
        onRefresh = fetchLessons
    ) {
        WeekView(state = state, lessonsToDays = lessonsToDays)
    }
}

@Composable
fun WeekView(
    state: WeekState,
    lessonsToDays: Map<WeekDay, List<Lesson>>
) {
    if (state.isLoading) return

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false,
        state = state.dayListState
    ) {
        items(6) { i ->
            val weekDay = WeekDay.getWorkDayByOrdinal(weekdayOrdinalToCalendarFormat(i))
            val lessons = lessonsToDays[weekDay] ?: emptyList()
            if (!state.hasSchedule) {
                MessageText(text = stringResource(R.string.no_schedule_selected))
            } else if (state.errorMessages.isNotEmpty()) {
                MessageText(text = stringResource(R.string.no_lessons_error_message))
            } else if (lessons.isEmpty()) {
                MessageText(text = stringResource(R.string.no_lessons_today))
            } else {
                LessonsList(
                    lessons = lessons,
                    modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp)
                )
            }
        }
    }
}

fun weekdayOrdinalToCalendarFormat(ordinal: Int): Int {
    val formatDiff = 2
    return if (ordinal == 6) 1 else ordinal + formatDiff
}

fun weekdayOrdinalFromCalendarFormat(ordinal: Int): Int {
    val formatDiff = 2
    return if (ordinal == 1) 6 else ordinal - formatDiff
}

suspend fun PointerInputScope.handleSwipe(
    minSwipeLength: Float = 50f,
    scrollToNextItem: () -> Unit,
    scrollToPreviousItem: () -> Unit,
    scrollToCurrentItem: () -> Unit,
    scrollBy: (Float) -> Unit
) {
    var swipeToNextDay = false
    var swipeToPreviousDay = false
    detectDragGestures(
        onDrag = { _, dragAmount ->
            if (dragAmount.x > minSwipeLength) {
                swipeToNextDay = false
                swipeToPreviousDay = true
            } else if (dragAmount.x < -minSwipeLength) {
                swipeToNextDay = true
                swipeToPreviousDay = false
            }
            scrollBy(-dragAmount.x)
        },
        onDragEnd = {
            if (swipeToNextDay) {
                scrollToNextItem()
            } else if (swipeToPreviousDay) {
                scrollToPreviousItem()
            } else {
                scrollToCurrentItem()
            }
            swipeToNextDay = false
            swipeToPreviousDay = false
        }
    )
}

@Composable
fun MessageText(
    modifier: Modifier = Modifier
        .width(LocalConfiguration.current.screenWidthDp.dp)
        .padding(PaddingValues(horizontal = 16.dp, vertical = 16.dp)),
    text: String = ""
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
fun LessonsList(modifier: Modifier = Modifier, lessons: List<Lesson>) {
    LazyColumn(modifier = modifier) {
        items(lessons.size) { index -> LessonItem(lesson = lessons[index]) }
    }
}

@Composable
fun LessonItem(modifier: Modifier = Modifier, lesson: Lesson) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = lesson.start, modifier = Modifier.padding(4.dp))
            Text(text = lesson.end, modifier = Modifier.padding(4.dp))
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(4.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(text = lesson.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 4.dp))
                Text(text = lesson.type, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                Text(text = lesson.place, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                Text(text = lesson.teacher, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
            }

        }
    }
}

private fun showConnectionToastOnWeekChange(
    context: Context,
    oldDay: WeekDay,
    newDay: WeekDay,
    connectionState: ConnectionState
) {
    val previousWeek = oldDay == WeekDay.MONDAY && newDay == WeekDay.SATURDAY
    val nextWeek = oldDay == WeekDay.SATURDAY && newDay == WeekDay.MONDAY
    val newWeek = nextWeek || previousWeek
    if (newWeek && connectionState == ConnectionState.UNAVAILABLE) {
        NoInternetConnectionToast.show(context, R.string.cannot_load_new_week_message)
    }
}

private object NoInternetConnectionToast {
    var toast: Toast? = null
    @StringRes var messageRes: Int? = null
    var locales: LocaleList? = null

    fun show(context: Context, @StringRes newMessageRes: Int) {
        val contextLocales = context.resources.configuration.locales
        toast?.cancel()
        if (toast == null || locales != contextLocales || messageRes != newMessageRes) {
            toast = Toast.makeText(context, newMessageRes, Toast.LENGTH_LONG)
            messageRes = newMessageRes
            locales = contextLocales
        }
        toast?.show()
    }
}


@Preview
@Composable
fun LessonItemPreview() {
    LessonItem(
        lesson = Lesson(
            id = 0,
            start = "10:00",
            end = "11:40",
            name = "Цифровая обработка сигналов",
            type = "Лабораторные",
            place = "3-й учебный корпус, 401",
            teacher = "Лупин Анатолий Викторович",
            lmsUrl = ""
        )
    )
}
