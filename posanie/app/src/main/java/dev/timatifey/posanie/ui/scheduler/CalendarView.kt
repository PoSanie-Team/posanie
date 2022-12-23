package dev.timatifey.posanie.ui.scheduler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier
        .width(300.dp)
        .height(400.dp),
    contentHeight: Dp = 300.dp,
    schedulerViewModel: SchedulerViewModel,
    close: () -> Unit
) {
    val schedulerUiState = schedulerViewModel.uiState.collectAsState().value

    val oldSelectedDate = schedulerUiState.selectedDate
    val newSelectedDateState = remember { mutableStateOf(oldSelectedDate) }

    val visibleMonthState = remember { mutableStateOf(oldSelectedDate.get(Calendar.MONTH)) }
    val visibleYearState = remember { mutableStateOf(oldSelectedDate.get(Calendar.YEAR)) }

    val currentItemState = remember { mutableStateOf(Int.MAX_VALUE / 2 + (visibleMonthState.value - (Int.MAX_VALUE / 2) % 12)) }
    val monthListState = rememberLazyListState(currentItemState.value)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        monthListState.scrollToItem(currentItemState.value)
    }

    LaunchedEffect(currentItemState.value) {
        monthListState.animateScrollToItem(currentItemState.value)
    }

    val monthScroller = createMonthScroller(
        coroutineScope = coroutineScope,
        monthListState = monthListState,
        visibleYearState = visibleYearState,
        visibleMonthState = visibleMonthState,
        currentItemState = currentItemState
    )

    Card(modifier = modifier) {
        Scaffold(
            topBar = {
                CalendarBar(
                    year = visibleYearState.value,
                    month = visibleMonthState.value,
                    goNextMonth = monthScroller.scrollToNextItem,
                    goPreviousMonth = monthScroller.scrollToPreviousItem
                )
            }
        ) { paddingValues ->
            CalendarDialog(
                modifier = modifier,
                paddingValues = paddingValues,
                contentHeight = contentHeight,
                monthScroller = monthScroller,
                monthListState = monthListState,
                visibleData = CalendarVisibleData(year = visibleYearState.value, month = visibleMonthState.value),
                oldSelectedDate = oldSelectedDate,
                newSelectedDateState = newSelectedDateState,
                selectDate = schedulerViewModel::selectDate,
                close = close
            )
        }
    }
}

fun createMonthScroller(
    coroutineScope: CoroutineScope,
    monthListState: LazyListState,
    visibleYearState: MutableState<Int>,
    visibleMonthState: MutableState<Int>,
    currentItemState: MutableState<Int>
): LazyListScroller {
    return LazyListScroller(
        scrollToNextItem = {
            if (visibleMonthState.value == 11) {
                visibleYearState.value++
                visibleMonthState.value = 0
            } else {
                visibleMonthState.value++
            }
            currentItemState.value++
        },
        scrollToPreviousItem = {
            if (visibleMonthState.value == 0) {
                visibleYearState.value--
                visibleMonthState.value = 11
            } else {
                visibleMonthState.value--
            }
            currentItemState.value--
        },
        scrollToCurrentItem = {
            coroutineScope.launch {
                monthListState.animateScrollToItem(currentItemState.value)
            }
        },
        scrollBy = { x ->
            coroutineScope.launch {
                monthListState.scrollBy(x)
            }
        }
    )
}

private class CalendarVisibleData(
    val year: Int,
    val month: Int
)

@Composable
private fun CalendarDialog(
    modifier: Modifier,
    paddingValues: PaddingValues,
    contentHeight: Dp,
    monthScroller: LazyListScroller,
    monthListState: LazyListState,
    visibleData: CalendarVisibleData,
    oldSelectedDate: Calendar,
    newSelectedDateState: MutableState<Calendar>,
    selectDate: (Calendar) -> Unit,
    close: () -> Unit,
) {
    Column(modifier = modifier.padding(paddingValues)) {
        ScrollableMonthList(
            modifier = Modifier
                .height(contentHeight)
                .then(modifier),
            monthScroller = monthScroller,
            monthListState = monthListState,
            visibleData = visibleData,
            oldSelectedDate = oldSelectedDate,
            newSelectedDate = newSelectedDateState.value,
            onDayClick = { day ->
                selectDay(
                    day = day,
                    visibleData = visibleData,
                    oldSelectedDate = oldSelectedDate,
                    newSelectedDateState = newSelectedDateState
                )
            }
        )
        DialogBar(
            newSelectedDate = newSelectedDateState.value,
            selectDate = selectDate,
            close = close
        )
    }
}

private fun selectDay(
    day: Int,
    visibleData: CalendarVisibleData,
    oldSelectedDate: Calendar,
    newSelectedDateState: MutableState<Calendar>
) {
    val newDate = Calendar.getInstance()
    newDate.set(visibleData.year, visibleData.month, day)

    val newSelectedDate = newSelectedDateState.value
    val sameYear = newDate.get(Calendar.YEAR) == newSelectedDate.get(Calendar.YEAR)
    val sameMonth = newDate.get(Calendar.MONTH) == newSelectedDate.get(Calendar.MONTH)
    val sameDay = newDate.get(Calendar.DAY_OF_MONTH) == newSelectedDate.get(Calendar.DAY_OF_MONTH)
    val sameDate = sameYear && sameMonth && sameDay
    newSelectedDateState.value = if (sameDate) oldSelectedDate else newDate
}

@Composable
fun DialogBar(
    newSelectedDate: Calendar,
    selectDate: (Calendar) -> Unit,
    close: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DialogButton(text = "Cancel", onClick = close)
        DialogButton(text = "Ok", onClick = {
            selectDate(newSelectedDate)
            close()
        })
    }
}

@Composable
private fun DialogButton(
    modifier: Modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .then(modifier)
    ) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CalendarBar(
    year: Int,
    month: Int,
    goNextMonth: () -> Unit,
    goPreviousMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = goPreviousMonth) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Localized description"
            )
        }
        CalendarDate(month = month, year = year)
        IconButton(onClick = goNextMonth) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Localized description"
            )
        }
    }
}

@Composable
private fun CalendarDate(modifier: Modifier = Modifier, month: Int, year: Int) {
    Text(
        text = "${stringResource(Month.getByOrdinal(month).fullNameId)} $year",
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun ScrollableMonthList(
    modifier: Modifier,
    monthScroller: LazyListScroller,
    monthListState: LazyListState,
    visibleData: CalendarVisibleData,
    oldSelectedDate: Calendar,
    newSelectedDate: Calendar,
    onDayClick: (Int) -> Unit
) {
    Box(modifier = modifier
        .height(0.dp)
        .pointerInput(true) {
            this.handleSwipe(
                minSwipeLength = 25f,
                scrollToNextItem = monthScroller.scrollToNextItem,
                scrollToPreviousItem = monthScroller.scrollToPreviousItem,
                scrollToCurrentItem = monthScroller.scrollToCurrentItem,
                scrollBy = monthScroller.scrollBy
            )
        }
    ) {
        MonthList(
            modifier = modifier,
            monthListState = monthListState,
            visibleData = visibleData,
            oldSelectedDate = oldSelectedDate,
            newSelectedDate = newSelectedDate,
            onDayClick = onDayClick
        )
    }
}

@Composable
private fun MonthList(
    modifier: Modifier = Modifier,
    monthListState: LazyListState,
    visibleData: CalendarVisibleData,
    oldSelectedDate: Calendar,
    newSelectedDate: Calendar,
    onDayClick: (Int) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        LazyRow(modifier = modifier, state = monthListState, userScrollEnabled = false) {
            items(Int.MAX_VALUE) { i ->
                val itemMonthIndex = i % 12
                val itemMonth = Month.getByOrdinal(itemMonthIndex)
                val itemYear = calculateItemYear(
                    visibleData = visibleData,
                    itemMonth = itemMonthIndex
                )

                val monthHasOldSelectedDay = monthHasSelectedDay(
                    year = itemYear,
                    month = itemMonthIndex,
                    selectedDate = oldSelectedDate
                )
                val selectedDayOld =
                    if (monthHasOldSelectedDay) oldSelectedDate.get(Calendar.DAY_OF_MONTH) else null

                val monthHasNewSelectedDay = monthHasSelectedDay(
                    year = itemYear,
                    month = itemMonthIndex,
                    selectedDate = newSelectedDate
                )
                val selectedDayNew =
                    if (monthHasNewSelectedDay) newSelectedDate.get(Calendar.DAY_OF_MONTH) else null

                MonthItem(
                    modifier = modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                    year = itemYear,
                    month = itemMonth,
                    selectedDayOld = selectedDayOld,
                    selectedDayNew = selectedDayNew,
                    onDayClick = onDayClick
                )
            }
        }
    }
}

private fun monthHasSelectedDay(month: Int, year: Int, selectedDate: Calendar): Boolean {
    val selectedDateYear = selectedDate.get(Calendar.YEAR)
    val yearHasSelectedDay = selectedDateYear == year

    val selectedDateMonth = selectedDate.get(Calendar.MONTH)
    val monthHasSelectedDay = selectedDateMonth == month

    return yearHasSelectedDay && monthHasSelectedDay
}

private fun calculateItemYear(visibleData: CalendarVisibleData, itemMonth: Int): Int {
    return if (itemMonth == 11 && visibleData.month == 0) {
        visibleData.year - 1
    } else if (itemMonth == 0 && visibleData.month == 11) {
        visibleData.year + 1
    } else visibleData.year
}

@Composable
private fun MonthItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    year: Int,
    month: Month,
    selectedDayOld: Int?,
    selectedDayNew: Int?,
    onDayClick: (Int) -> Unit
) {
    val daysInMonth = Month.getDaysCount(year, month)
    Column {
        CalendarWeekBar(modifier = modifier)
        CalendarDays(
            modifier = modifier,
            year = year,
            month = month,
            daysInMonth = daysInMonth,
            selectedDayOld = selectedDayOld,
            selectedDayNew = selectedDayNew,
            onDayClick = onDayClick
        )
    }
}

@Composable
private fun CalendarWeekBar(
    modifier: Modifier
) {
    LazyVerticalGrid(
        modifier = Modifier
            .height(30.dp)
            .then(modifier),
        columns = GridCells.Fixed(7),
        userScrollEnabled = false
    ) {
        items(7) { i ->
            WeekDayTab(weekDay = WeekDay.getDayByOrdinal(weekdayOrdinalToCalendarFormat(i)))
        }
    }
}

@Composable
private fun CalendarDays(
    modifier: Modifier,
    year: Int,
    month: Month,
    daysInMonth: Int,
    selectedDayOld: Int?,
    selectedDayNew: Int?,
    onDayClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(7),
        userScrollEnabled = false
    ) {
        val firstDayDate = Calendar.getInstance()
        firstDayDate.set(year, month.ordinal, 1)
        val emptyDaysCount = weekdayOrdinalFromCalendarFormat(firstDayDate.get(Calendar.DAY_OF_WEEK))
        items(daysInMonth + emptyDaysCount) { i ->
            if (i < emptyDaysCount) {
                EmptyCalendarDay()
            } else {
                val day = dayToHumanFormat(i - emptyDaysCount)
                val date = Calendar.getInstance()
                date.set(year, month.ordinal, day)
                CalendarDay(
                    day = day,
                    isSunday = date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY,
                    selectedOld = day == selectedDayOld,
                    selectedNew = day == selectedDayNew,
                    onClick = onDayClick
                )
            }
        }
    }
}

@Composable
private fun WeekDayTab(weekDay: WeekDay, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        Box(
            Modifier
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            val textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            Text(text = stringResource(weekDay.shortNameId), color = textColor)
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSunday: Boolean,
    selectedOld: Boolean,
    selectedNew: Boolean,
    onClick: (Int) -> Unit
) {
    val backgroundModifier = Modifier
        .aspectRatio(1f)
        .background(
            color = dayColor(selectedOld = selectedOld, selectedNew = selectedNew),
            shape = CircleShape
        )
    val clickableModifier =
        if (isSunday) backgroundModifier else backgroundModifier
            .clip(CircleShape)
            .clickable { onClick(day) }
    Box(
        modifier = clickableModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = dayTextColor(isSunday = isSunday),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyCalendarDay() {
    Box(
        modifier = Modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
    }
}

@Composable
private fun dayColor(selectedOld: Boolean, selectedNew: Boolean): Color {
    return if (selectedOld) {
        MaterialTheme.colorScheme.primaryContainer
    } else if (selectedNew) {
        MaterialTheme.colorScheme.secondaryContainer
    }  else {
        Color.Transparent
    }
}

@Composable
private fun dayTextColor(isSunday: Boolean): Color {
    return if (isSunday) {
        MaterialTheme.colorScheme.error
    } else {
        Color.Unspecified
    }
}

fun dayToHumanFormat(day: Int): Int {
    return day + 1
}