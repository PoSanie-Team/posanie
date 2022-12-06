package dev.timatifey.posanie.ui.scheduler

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun SchedulerBar(
    schedulerViewModel: SchedulerViewModel,
    schedulerUiState: SchedulerUiState.UiState,
    openCalendar: () -> Unit
) {
    SchedulerBar(
        selectedDate = schedulerUiState.selectedDate,
        selectedDay = schedulerUiState.selectedDay,
        oddWeek = schedulerUiState.weekIsOdd,
        hasSchedule = schedulerUiState.hasSchedule && !schedulerUiState.isLoading,
        selectDay = schedulerViewModel::selectWeekDay,
        goNextWeek = {
            schedulerViewModel.setNextMonday()
            schedulerViewModel.selectWeekDay(WeekWorkDay.MONDAY)
        },
        goPreviousWeek = {
            schedulerViewModel.setPreviousMonday()
            schedulerViewModel.selectWeekDay(WeekWorkDay.MONDAY)
        },
        openCalendar = openCalendar
    )
}

@Composable
fun SchedulerBar(
    selectedDate: Calendar,
    selectedDay: WeekWorkDay,
    oddWeek: Boolean,
    hasSchedule: Boolean,
    selectDay: (WeekWorkDay) -> Unit,
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
                contentDescription = "Localized description"
            )
        }
        WeekDate(date = date, oddWeek = oddWeek, hasSchedule = hasSchedule, modifier = Modifier.clickable { openCalendar() })
        IconButton(onClick = goNextWeek) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Localized description"
            )
        }
    }
}

@Composable
fun WeekDate(date: Calendar, oddWeek: Boolean, hasSchedule: Boolean, modifier: Modifier = Modifier) {
    val day = date.get(Calendar.DAY_OF_MONTH)
    val formattedDay = if (day < 10) "0$day" else "$day"
    val month = date.get(Calendar.MONTH) + 1
    val formattedMonth = if (month < 10) "0$month" else "$month"
    val year = date.get(Calendar.YEAR)
    val week = if (oddWeek) "odd" else "even"
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$formattedDay.$formattedMonth.$year", textAlign = TextAlign.Center)
        Text(text= if (hasSchedule) week else "", textAlign = TextAlign.Center)
    }
}

@Composable
fun WeekBar(selectedDay: WeekWorkDay, onDayClick: (WeekWorkDay) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Day(
            weekDay = WeekWorkDay.MONDAY,
            selected = selectedDay == WeekWorkDay.MONDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekWorkDay.MONDAY)
        }
        Day(
            weekDay = WeekWorkDay.TUESDAY,
            selected = selectedDay == WeekWorkDay.TUESDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekWorkDay.TUESDAY)
        }
        Day(
            weekDay = WeekWorkDay.WEDNESDAY,
            selected = selectedDay == WeekWorkDay.WEDNESDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekWorkDay.WEDNESDAY)
        }
        Day(
            weekDay = WeekWorkDay.THURSDAY,
            selected = selectedDay == WeekWorkDay.THURSDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekWorkDay.THURSDAY)
        }
        Day(
            weekDay = WeekWorkDay.FRIDAY,
            selected = selectedDay == WeekWorkDay.FRIDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekWorkDay.FRIDAY)
        }
        Day(
            weekDay = WeekWorkDay.SATURDAY,
            selected = selectedDay == WeekWorkDay.SATURDAY,
            modifier = Modifier.weight(1f)) { onDayClick(WeekWorkDay.SATURDAY)
        }
    }
}

@Composable
fun Day(weekDay: WeekWorkDay, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
            Text(text = weekDay.shortName)
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
        selectedDay = WeekWorkDay.MONDAY,
        oddWeek = false,
        hasSchedule = true,
        selectDay = {},
        goNextWeek = {},
        goPreviousWeek = {},
        openCalendar = {}
    )
}