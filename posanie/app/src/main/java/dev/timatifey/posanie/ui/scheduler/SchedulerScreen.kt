package dev.timatifey.posanie.ui.scheduler

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.domain.Lesson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(
    schedulerViewModel: SchedulerViewModel,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit
) {
    val schedulerUiState = schedulerViewModel.uiState.collectAsState().value

    val lessonsToDays = (schedulerUiState as SchedulerUiState.UiState).lessonsToDays

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
                    openCalendar = { calendarVisibilityState.value = true }
                )
            }
        ) { paddingValues ->
            if (schedulerUiState.hasSchedule) {
                val weekState = WeekState(
                    dayListState = weekDayListState,
                    isLoading = schedulerUiState.isLoading
                )
                val weekScroller = createWeekScroller(
                    schedulerViewModel = schedulerViewModel,
                    coroutineScope = coroutineScope,
                    weekDayListState = weekDayListState
                )
                ScrollableWeek(
                    modifier = Modifier.padding(paddingValues),
                    state = weekState,
                    lessonsToDays = lessonsToDays,
                    weekScroller = weekScroller,
                    fetchLessons = schedulerViewModel::fetchLessons
                )
            } else {
                Box(modifier = Modifier.padding(paddingValues)) {
                    MessageText(text = "Please choose group or teacher first.")
                }
            }
        }

        LaunchedEffect(true) {
            createPopup(calendarVisibilityState) {
                Calendar()
            }
        }
    }
}

@Composable
fun ScrollableWeek(
    modifier: Modifier = Modifier,
    state: WeekState,
    lessonsToDays: Map<WeekDay, List<Lesson>>,
    weekScroller: WeekScroller,
    fetchLessons: () -> Unit
) {
    Box(modifier = modifier
        .fillMaxSize()
        .pointerInput(true) {
            this.handleWeekDaySwipe(
                scrollToNextWeekDay = weekScroller.scrollToNextWeekDay,
                scrollToPreviousWeekDay = weekScroller.scrollToPreviousWeekDay,
                scrollToCurrentWeekDay = weekScroller.scrollToCurrentWeekDay,
                scrollBy = weekScroller.scrollBy
            )
        }) {
        RefreshableWeek(
            state = state,
            lessonsToDays = lessonsToDays,
            fetchLessons = fetchLessons
        )
    }
}

class WeekState(
    val dayListState: LazyListState,
    val isLoading: Boolean,
)

class WeekScroller(
    val scrollToNextWeekDay: () -> Unit,
    val scrollToPreviousWeekDay: () -> Unit,
    val scrollToCurrentWeekDay: () -> Unit,
    val scrollBy: (Float) -> Unit
)

fun createWeekScroller(
    schedulerViewModel: SchedulerViewModel,
    coroutineScope: CoroutineScope,
    weekDayListState: LazyListState
): WeekScroller {
    return WeekScroller(
        scrollToPreviousWeekDay = schedulerViewModel::selectPreviousWeekDay,
        scrollToNextWeekDay = schedulerViewModel::selectNextWeekDay,
        scrollToCurrentWeekDay = {
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
        Week(state = state, lessonsToDays = lessonsToDays)
    }
}

@Composable
fun Week(
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
            val weekDay = WeekDay.getByOrdinal(weekdayOrdinalToCalendarFormat(i))
            val lessons = lessonsToDays[weekDay] ?: emptyList()
            if (lessons.isEmpty()) {
                MessageText(text = "No lessons today.")
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
    return ordinal + formatDiff
}

suspend fun PointerInputScope.handleWeekDaySwipe(
    scrollToNextWeekDay: () -> Unit,
    scrollToPreviousWeekDay: () -> Unit,
    scrollToCurrentWeekDay: () -> Unit,
    scrollBy: (Float) -> Unit
) {
    val minSwipeLength = 50f
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
                scrollToNextWeekDay()
            } else if (swipeToPreviousDay) {
                scrollToPreviousWeekDay()
            } else {
                scrollToCurrentWeekDay()
            }
            swipeToNextDay = false
            swipeToPreviousDay = false
        }
    )
}

@Composable
fun MessageText(
    modifier: Modifier = Modifier
        .padding(16.dp)
        .width(LocalConfiguration.current.screenWidthDp.dp),
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

@Composable
fun Calendar(
    modifier: Modifier = Modifier
        .width(200.dp)
        .height(300.dp),
) {
    Box(
        modifier = modifier.background(Color.Green),
    )
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

@Preview
@Composable
fun ShortLessonItemPreview() {
    LessonItem(
        lesson = Lesson(
            id = 0,
            start = "10:00",
            end = "11:40",
            name = "Военная подготовка",
            type = "Лабораторные",
            place = "3-й учебный корпус, 401",
            teacher = "Лупин Анатолий Викторович",
            lmsUrl = ""
        )
    )
}
