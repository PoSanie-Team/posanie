package dev.timatifey.posanie.ui.scheduler

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier
        .width(300.dp)
        .height(450.dp),
    schedulerViewModel: SchedulerViewModel
) {
    val schedulerUiState = schedulerViewModel.uiState.collectAsState().value

    var newSelectedDate by remember { mutableStateOf(schedulerUiState.selectedDate) }
    var visibleMonth by remember { mutableStateOf(schedulerUiState.selectedDate.get(Calendar.MONTH)) }
    var visibleYear by remember { mutableStateOf(schedulerUiState.selectedDate.get(Calendar.YEAR)) }

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
            Box(modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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
                LazyRow(state = monthListState, userScrollEnabled = false) {
                    items(Int.MAX_VALUE) { i ->
                        val index = i % 12
                        val month = Month.getByOrdinal(index)
                        //Text(text = month.fullName, modifier = modifier)
                        MonthDays(daysInMonth = Month.getDaysCount(visibleYear, month), modifier = modifier.fillMaxHeight())
                    }
                }
            }
        }
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
fun MonthDays(modifier: Modifier = Modifier, daysInMonth: Int) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(7)
    ) {
        items(daysInMonth) { i ->
            CalendarDay(i + 1)
        }
    }
}

@Composable
fun CalendarDay(day: Int) {
    Text(
        text = day.toString(),
        textAlign = TextAlign.Center,
        modifier = Modifier
    )
}