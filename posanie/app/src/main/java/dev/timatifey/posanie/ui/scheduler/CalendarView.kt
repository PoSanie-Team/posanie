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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier
        .width(300.dp)
        .height(300.dp),
    schedulerViewModel: SchedulerViewModel
) {
    val schedulerUiState = schedulerViewModel.uiState.collectAsState().value
    val oldSelectedDate = schedulerUiState.selectedDate

    var newSelectedDate by remember { mutableStateOf(oldSelectedDate) }
    var visibleMonth by remember { mutableStateOf(oldSelectedDate.get(Calendar.MONTH)) }
    var visibleYear by remember { mutableStateOf(oldSelectedDate.get(Calendar.YEAR)) }

    var currentItem by remember { mutableStateOf(Int.MAX_VALUE / 2) }
    currentItem += visibleMonth - currentItem % 12
    val monthListState = rememberLazyListState(currentItem)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        monthListState.scrollToItem(currentItem)
    }

    LaunchedEffect(currentItem) {
        monthListState.animateScrollToItem(currentItem)
    }

    val monthScroller = LazyListScroller(
        scrollToNextItem = {
            if (visibleMonth == 11) {
                visibleYear++
                visibleMonth = 0
            } else {
                visibleMonth++
            }
            currentItem++
        },
        scrollToPreviousItem = {
            if (visibleMonth == 0) {
                visibleYear--
                visibleMonth = 11
            } else {
                visibleMonth--
            }
            currentItem--
        },
        scrollToCurrentItem = {
            coroutineScope.launch {
                monthListState.animateScrollToItem(currentItem)
            }
        },
        scrollBy = { x ->
            coroutineScope.launch {
                monthListState.scrollBy(x)
            }
        }
    )

    Card(modifier = modifier) {
        Scaffold(
            topBar = {
                CalendarBar(
                    month = visibleMonth,
                    year = visibleYear,
                    goNextMonth = monthScroller.scrollToNextItem,
                    goPreviousMonth = monthScroller.scrollToPreviousItem
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                ScrollableMonthList(
                    modifier = Modifier
                        .height(200.dp)
                        .then(modifier),
                    monthScroller = monthScroller,
                    monthListState = monthListState,
                    visibleYear = visibleYear,
                    visibleMonth = visibleMonth,
                    oldSelectedDate = oldSelectedDate,
                    newSelectedDate = newSelectedDate
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DialogButton(text = "Cancel", onClick = {})
                    DialogButton(text = "Ok", onClick = {})
                }
            }
        }
    }
}

@Composable
fun DialogButton(
    modifier: Modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable { onClick() }.then(modifier)
    ) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Composable
fun CalendarBar(
    month: Int,
    year: Int,
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
fun CalendarDate(modifier: Modifier = Modifier, month: Int, year: Int) {
    Text(text = "${Month.getByOrdinal(month).fullName} $year", textAlign = TextAlign.Center, modifier = modifier)
}

@Composable
fun ScrollableMonthList(
    modifier: Modifier,
    monthScroller: LazyListScroller,
    monthListState: LazyListState,
    visibleYear: Int,
    visibleMonth: Int,
    oldSelectedDate: Calendar,
    newSelectedDate: Calendar
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
            visibleYear = visibleYear,
            visibleMonth = visibleMonth,
            oldSelectedDate = oldSelectedDate,
            newSelectedDate = newSelectedDate
        )
    }
}

@Composable
fun MonthList(
    modifier: Modifier = Modifier,
    monthListState: LazyListState,
    visibleYear: Int,
    visibleMonth: Int,
    oldSelectedDate: Calendar,
    newSelectedDate: Calendar
) {
    Box(
        modifier = modifier
    ) {
        LazyRow(modifier = modifier, state = monthListState, userScrollEnabled = false) {
            items(Int.MAX_VALUE) { i ->
                val itemMonthIndex = i % 12
                val itemMonth = Month.getByOrdinal(itemMonthIndex)
                val itemYear = calculateItemYear(
                    visibleYear = visibleYear,
                    visibleMonth = visibleMonth,
                    itemMonth = itemMonthIndex
                )

                val monthHasOldSelectedDay = monthHasSelectedDay(
                    year = itemYear,
                    month = itemMonthIndex,
                    selectedDate = oldSelectedDate
                )
                val selectedDayOld =
                    if (monthHasOldSelectedDay) newSelectedDate.get(Calendar.DAY_OF_MONTH) else null

                val monthHasNewSelectedDay = monthHasSelectedDay(
                    year = itemYear,
                    month = itemMonthIndex,
                    selectedDate = newSelectedDate
                )
                val selectedDayNew =
                    if (monthHasNewSelectedDay) newSelectedDate.get(Calendar.DAY_OF_MONTH) else null

                MonthItem(
                    modifier = modifier,
                    daysInMonth = Month.getDaysCount(visibleYear, itemMonth),
                    selectedDayOld = selectedDayOld,
                    selectedDayNew = selectedDayNew
                )
            }
        }
    }
}

fun monthHasSelectedDay(month: Int, year: Int, selectedDate: Calendar): Boolean {
    val selectedDateYear = selectedDate.get(Calendar.YEAR)
    val yearHasSelectedDay = selectedDateYear == year

    val selectedDateMonth = selectedDate.get(Calendar.MONTH)
    val monthHasSelectedDay = selectedDateMonth == month

    return yearHasSelectedDay && monthHasSelectedDay
}

fun calculateItemYear(visibleYear: Int, visibleMonth: Int, itemMonth: Int): Int {
    return if (itemMonth == 11 && visibleMonth == 0) {
        visibleYear - 1
    } else if (itemMonth == 0 && visibleMonth == 11) {
        visibleYear + 1
    } else visibleYear
}

@Composable
fun MonthItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    daysInMonth: Int,
    selectedDayOld: Int?,
    selectedDayNew: Int?
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(7)
    ) {
        items(daysInMonth) { i ->
            val day = dayToHumanFormat(i)
            CalendarDay(
                day = day,
                selectedOld = day == selectedDayOld,
                selectedNew = day == selectedDayNew
            )
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    selectedOld: Boolean,
    selectedNew: Boolean
) {
    Text(
        text = day.toString(),
        textAlign = TextAlign.Center,
        modifier = Modifier.background(dayColor(selectedOld = selectedOld, selectedNew = selectedNew))
    )
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

fun dayToHumanFormat(day: Int): Int {
    return day + 1
}