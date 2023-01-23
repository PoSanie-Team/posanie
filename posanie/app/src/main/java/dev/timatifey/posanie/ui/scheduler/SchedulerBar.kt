package dev.timatifey.posanie.ui.scheduler

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.R
import dev.timatifey.posanie.ui.ConnectionState
import java.util.*

@Composable
fun SchedulerBar(
    schedulerViewModel: SchedulerViewModel,
    schedulerUiState: SchedulerUiState,
    openCalendar: () -> Unit,
    showNoInternetConnectionToast: () -> Unit
) {
    SchedulerBar(
        selectedDate = schedulerUiState.selectedDate,
        selectedDay = schedulerUiState.selectedDay,
        oddWeek = schedulerUiState.weekIsOdd,
        hasSchedule = schedulerUiState.hasSchedule && !schedulerUiState.isLoading,
        selectDay = schedulerViewModel::selectWeekDay,
        goNextWeek = {
            schedulerViewModel.setNextMonday()
            schedulerViewModel.selectWeekDay(WeekDay.MONDAY)
            if (schedulerViewModel.uiState.value.connectionState == ConnectionState.UNAVAILABLE) {
                showNoInternetConnectionToast()
            }
        },
        goPreviousWeek = {
            schedulerViewModel.setPreviousMonday()
            schedulerViewModel.selectWeekDay(WeekDay.MONDAY)
            if (schedulerViewModel.uiState.value.connectionState == ConnectionState.UNAVAILABLE) {
                showNoInternetConnectionToast()
            }
        },
        openCalendar = openCalendar
    )
}

@Composable
fun SchedulerBar(
    selectedDate: Calendar,
    selectedDay: WeekDay,
    oddWeek: Boolean,
    hasSchedule: Boolean,
    selectDay: (WeekDay) -> Unit,
    goNextWeek: () -> Unit,
    goPreviousWeek: () -> Unit,
    openCalendar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        DateBar(
            date = selectedDate,
            oddWeek = oddWeek,
            hasSchedule = hasSchedule,
            goNextWeek = goNextWeek,
            goPreviousWeek = goPreviousWeek,
            openCalendar = openCalendar
        )
        WeekBar(selectedDay = selectedDay, onDayClick = { day -> selectDay(day) })
    }
}

@Composable
fun DateBar(
    date: Calendar,
    oddWeek: Boolean,
    hasSchedule: Boolean,
    goNextWeek: () -> Unit,
    goPreviousWeek: () -> Unit,
    openCalendar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = goPreviousWeek) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(R.string.previous_week_button_description)
            )
        }
        WeekDate(date = date, oddWeek = oddWeek, hasSchedule = hasSchedule, modifier = Modifier.clickable { openCalendar() })
        IconButton(onClick = goNextWeek) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = stringResource(R.string.next_week_button_description)
            )
        }
    }
}

@Composable
fun WeekDate(modifier: Modifier = Modifier, date: Calendar, oddWeek: Boolean, hasSchedule: Boolean) {
    val day = date.get(Calendar.DAY_OF_MONTH)
    val formattedDay = if (day < 10) "0$day" else "$day"
    val month = date.get(Calendar.MONTH) + 1
    val formattedMonth = if (month < 10) "0$month" else "$month"
    val year = date.get(Calendar.YEAR)
    val week = if (oddWeek) stringResource(R.string.odd_week) else stringResource(R.string.even_week)
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$formattedDay.$formattedMonth.$year", textAlign = TextAlign.Center)
        Text(text= if (hasSchedule) week else "", textAlign = TextAlign.Center)
    }
}

@Composable
fun WeekBar(selectedDay: WeekDay, onDayClick: (WeekDay) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Day(
            weekDay = WeekDay.MONDAY,
            selected = selectedDay == WeekDay.MONDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekDay.MONDAY)
        }
        Day(
            weekDay = WeekDay.TUESDAY,
            selected = selectedDay == WeekDay.TUESDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekDay.TUESDAY)
        }
        Day(
            weekDay = WeekDay.WEDNESDAY,
            selected = selectedDay == WeekDay.WEDNESDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekDay.WEDNESDAY)
        }
        Day(
            weekDay = WeekDay.THURSDAY,
            selected = selectedDay == WeekDay.THURSDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekDay.THURSDAY)
        }
        Day(
            weekDay = WeekDay.FRIDAY,
            selected = selectedDay == WeekDay.FRIDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekDay.FRIDAY)
        }
        Day(
            weekDay = WeekDay.SATURDAY,
            selected = selectedDay == WeekDay.SATURDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekDay.SATURDAY)
        }
    }
}

@Composable
fun Day(modifier: Modifier = Modifier, weekDay: WeekDay, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        Box(
            Modifier
                .clickable(
                    onClick = onClick,
                    enabled = true,
                    role = Role.Tab,
                )
                .background(dayColor(selected))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(weekDay.shortNameId))
        }
    }
}

@Composable
private fun dayColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
}

@Preview
@Composable
fun previewSchedulerBar() {
    SchedulerBar(
        selectedDate = Calendar.getInstance(),
        selectedDay = WeekDay.MONDAY,
        oddWeek = false,
        hasSchedule = true,
        selectDay = {},
        goNextWeek = {},
        goPreviousWeek = {},
        openCalendar = {}
    )
}