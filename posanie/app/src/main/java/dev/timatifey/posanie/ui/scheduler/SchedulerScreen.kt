package dev.timatifey.posanie.ui.scheduler

import android.content.ClipDescription
import android.content.Context
import android.os.LocaleList
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.model.domain.Lesson
import dev.timatifey.posanie.ui.ConnectionState
import dev.timatifey.posanie.ui.theme.PoSanieTheme
import dev.timatifey.posanie.utils.ErrorMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    LaunchedEffect(schedulerUiState.selectedDay) {
        coroutineScope.launch {
            weekDayListState.animateScrollToItem(currentWeekDayOrdinal)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                SchedulerBar(
                    schedulerViewModel = schedulerViewModel,
                    schedulerUiState = schedulerUiState,
                    openCalendar = { calendarVisibilityState.value = true },
                    showCannotLoadWeekToast = {
                        NoInternetConnectionToast.show(
                            context,
                            R.string.cannot_load_new_week_message
                        )
                    }
                )
            }
        ) { paddingValues ->
            val weekState = WeekState(
                dayListState = weekDayListState,
                errorMessages = schedulerUiState.errorMessages,
                isLoading = schedulerUiState.isLoading,
                hasSchedule = schedulerUiState.hasSchedule,
                expandedLessons = schedulerUiState.expandedLessons
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
                expandLesson = schedulerViewModel::expandLesson,
                hideLesson = schedulerViewModel::hideLesson,
                weekScroller = weekScroller,
                fetchLessons = {
                    schedulerViewModel.fetchLessons()
                    if (schedulerUiState.connectionState == ConnectionState.UNAVAILABLE) {
                        NoInternetConnectionToast.show(
                            context,
                            R.string.cannot_update_schedule_message
                        )
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
    expandLesson: (Lesson) -> Unit,
    hideLesson: (Lesson) -> Unit,
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
            fetchLessons = fetchLessons,
            expandLesson = expandLesson,
            hideLesson = hideLesson
        )
    }
}

class WeekState(
    val dayListState: LazyListState,
    val errorMessages: List<ErrorMessage>,
    val isLoading: Boolean,
    val hasSchedule: Boolean,
    val expandedLessons: List<Lesson>
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
    fetchLessons: () -> Unit,
    expandLesson: (Lesson) -> Unit,
    hideLesson: (Lesson) -> Unit
) {
    SwipeRefresh(
        modifier = Modifier
            .fillMaxSize(),
        state = rememberSwipeRefreshState(state.isLoading),
        onRefresh = fetchLessons
    ) {
        WeekView(
            state = state,
            lessonsToDays = lessonsToDays,
            expandLesson = expandLesson,
            hideLesson = hideLesson
        )
    }
}

@Composable
fun WeekView(
    state: WeekState,
    lessonsToDays: Map<WeekDay, List<Lesson>>,
    expandLesson: (Lesson) -> Unit,
    hideLesson: (Lesson) -> Unit
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
                    modifier = Modifier
                        .width(LocalConfiguration.current.screenWidthDp.dp)
                        .padding(bottom = 8.dp),
                    lessons = lessons,
                    expandedLessons = state.expandedLessons,
                    expandLesson = expandLesson,
                    hideLesson = hideLesson
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
fun LessonsList(
    modifier: Modifier = Modifier,
    lessons: List<Lesson>,
    expandedLessons: List<Lesson>,
    expandLesson: (Lesson) -> Unit,
    hideLesson: (Lesson) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(lessons.size) { index ->
            val lesson = lessons[index]
            val previousLesson = if (index > 0) lessons[index - 1] else null
            val sameStart = previousLesson?.start == lesson.start
            val sameEnd = previousLesson?.end == lesson.end
            LessonItem(
                lesson = lessons[index],
                sameTime = sameStart && sameEnd,
                isExpanded = expandedLessons.contains(lesson),
                expandLesson = expandLesson,
                hideLesson = hideLesson
            )
        }
    }
}

@Composable
fun LessonItem(
    modifier: Modifier = Modifier,
    lesson: Lesson,
    sameTime: Boolean,
    isExpanded: Boolean,
    expandLesson: (Lesson) -> Unit,
    hideLesson: (Lesson) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (!sameTime) {
            LessonTime(modifier = modifier, lesson = lesson)
        }
        LessonCard(
            lesson = lesson,
            isExpanded = isExpanded,
            expandLesson = expandLesson,
            hideLesson = hideLesson
        )
    }
}

@Composable
fun LessonTime(modifier: Modifier = Modifier, lesson: Lesson) {
    val lessonTimeDescription = stringResource(R.string.lesson_time_description, lesson.id)
    Row(
        modifier = modifier
            .padding(start = 8.dp, bottom = 8.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .semantics { contentDescription = lessonTimeDescription },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = lesson.start,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "—",
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = lesson.end,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun LessonIndexLabel(lessonIndex: Int) {
    Card(
        modifier = Modifier
            .padding(end = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Text(
            text = (lessonIndex + 1).toString(),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    isExpanded: Boolean,
    expandLesson: (Lesson) -> Unit,
    hideLesson: (Lesson) -> Unit
) {
    val lessonCardDescription = stringResource(R.string.lesson_card_description, lesson.id)
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable { if (isExpanded) hideLesson(lesson) else expandLesson(lesson) }
            .semantics { contentDescription = lessonCardDescription },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            val nameTextDescription = stringResource(R.string.lesson_name_text_description, lesson.id)
            val typeTextDescription = stringResource(R.string.lesson_type_text_description, lesson.id)
            val placeTextDescription = stringResource(R.string.lesson_place_text_description, lesson.id)
            val teacherTextDescription = stringResource(R.string.lesson_teacher_text_description, lesson.id)
            val groupNamesDescription = stringResource(R.string.lesson_group_names_text_description, lesson.id)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .weight(1f)
                        .semantics {
                            contentDescription = nameTextDescription
                        },
                    text = lesson.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                ExpandIcon(
                    isExpanded = isExpanded,
                    iconResource = R.drawable.ic_keyboard_arrow_down,
                    iconDescription = R.string.expand_lesson_card_icon_description,
                )
            }
            Text(
                text = lesson.type,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .semantics {
                        contentDescription = typeTextDescription
                    }
            )
            Text(
                text = lesson.place,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .semantics {
                        contentDescription = placeTextDescription
                    }
            )
            if (lesson.teacher.isNotEmpty()) {
                Text(
                    text = lesson.teacher,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .semantics {
                            contentDescription = teacherTextDescription
                        }
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                val groupNames = joinGroupsToString(LocalContext.current, lesson.groupNames)
                Text(
                    text = groupNames,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .semantics {
                            contentDescription = groupNamesDescription
                        }
                )
            }
        }
    }
}

fun joinGroupsToString(context: Context, groupNames: List<String>): String {
    val groupNamesStringBuilder = StringBuilder()
    groupNamesStringBuilder.append(context.getString(R.string.groups))
    groupNamesStringBuilder.append(": ")
    for (i in 0 until groupNames.size - 1) {
        groupNamesStringBuilder.append("${groupNames[i]}, ")
    }
    groupNamesStringBuilder.append(groupNames.last())
    return groupNamesStringBuilder.toString()
}

@Composable
fun ExpandIcon(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    @DrawableRes iconResource: Int,
    @StringRes iconDescription: Int
) {
    val rotation: Float by animateFloatAsState(if (isExpanded) 180f else 0f)
    Icon(
        modifier = modifier.rotate(rotation),
        tint = MaterialTheme.colorScheme.primary,
        painter = painterResource(iconResource),
        contentDescription = stringResource(iconDescription)
    )
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

    @StringRes
    var messageRes: Int? = null
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

@Composable
fun LessonItemPreview() {
    PoSanieTheme(appTheme = AppTheme.LIGHT, appColorScheme = AppColorScheme.GREEN) {
        LessonItem(
            lesson = Lesson(
                id = 0,
                start = "10:00",
                end = "11:40",
                name = "Цифровая обработка сигналов",
                type = "Лабораторные",
                place = "3-й учебный корпус, 401",
                teacher = "Лупин Анатолий Викторович",
                groupNames = listOf("3530901/90202"),
                lmsUrl = ""
            ),
            sameTime = false,
            isExpanded = false,
            expandLesson = {},
            hideLesson = {}
        )
    }
}

@Preview
@Composable
fun LessonListPreview() {
    PoSanieTheme(appTheme = AppTheme.LIGHT, appColorScheme = AppColorScheme.GREEN) {
        LessonsList(
            modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp),
            lessons = buildList { repeat(4) { add(PreviewLessonItem()) } },
            expandedLessons = listOf(PreviewLessonItem()),
            expandLesson = {},
            hideLesson = {}
        )
    }
}

fun PreviewLessonItem() = Lesson(
    id = 0,
    start = "10:00",
    end = "11:40",
    name = "Цифровая обработка сигналов и еще очень длинное название",
    type = "Лабораторные",
    place = "3-й учебный корпус, 401",
    teacher = "Лупин Анатолий Викторович",
    groupNames = listOf("3530901/90202"),
    lmsUrl = ""
)
