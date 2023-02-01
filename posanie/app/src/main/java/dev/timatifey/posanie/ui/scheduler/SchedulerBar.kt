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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.R
import dev.timatifey.posanie.ui.ConnectionState
import java.util.*

private val WEEK_DAYS = listOf(
    WeekDay.MONDAY,
    WeekDay.TUESDAY,
    WeekDay.WEDNESDAY,
    WeekDay.THURSDAY,
    WeekDay.FRIDAY,
    WeekDay.SATURDAY
)

@Composable
fun SchedulerBar(
    schedulerViewModel: SchedulerViewModel,
    schedulerUiState: SchedulerUiState,
    openCalendar: () -> Unit,
    showCannotLoadWeekToast: () -> Unit
) {
    val currentDate = schedulerUiState.mondayDate.clone() as Calendar
    SchedulerBar(
        selectedDate = schedulerUiState.selectedDate,
        selectedDay = schedulerUiState.selectedDay,
        weekDayToMonthDay = buildMap {
            WEEK_DAYS.forEach { weekDay ->
                put(weekDay, currentDate.get(Calendar.DAY_OF_MONTH))
                currentDate.add(Calendar.DATE, 1)
            }
        },
        oddWeek = schedulerUiState.weekIsOdd,
        hasSchedule = schedulerUiState.hasSchedule && !schedulerUiState.isLoading,
        selectDay = schedulerViewModel::selectWeekDay,
        goNextWeek = {
            schedulerViewModel.setNextMonday()
            schedulerViewModel.selectWeekDay(WeekDay.MONDAY)
            if (schedulerViewModel.uiState.value.connectionState == ConnectionState.UNAVAILABLE) {
                showCannotLoadWeekToast()
            }
        },
        goPreviousWeek = {
            schedulerViewModel.setPreviousMonday()
            schedulerViewModel.selectWeekDay(WeekDay.MONDAY)
            if (schedulerViewModel.uiState.value.connectionState == ConnectionState.UNAVAILABLE) {
                showCannotLoadWeekToast()
            }
        },
        openCalendar = openCalendar
    )
}

@Composable
fun SchedulerBar(
    selectedDate: Calendar,
    selectedDay: WeekDay,
    weekDayToMonthDay: Map<WeekDay, Int>,
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
        WeekBar(
            selectedDay = selectedDay,
            weekDayToMonthDay = weekDayToMonthDay,
            onDayClick = { day -> selectDay(day) }
        )
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
        WeekDate(
            date = date,
            oddWeek = oddWeek,
            hasSchedule = hasSchedule,
            modifier = Modifier.clickable { openCalendar() })
        IconButton(onClick = goNextWeek) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = stringResource(R.string.next_week_button_description)
            )
        }
    }
}

@Composable
fun WeekDate(
    modifier: Modifier = Modifier,
    date: Calendar,
    oddWeek: Boolean,
    hasSchedule: Boolean
) {
    val day = date.get(Calendar.DAY_OF_MONTH)
    val formattedDay = if (day < 10) "0$day" else "$day"
    val month = date.get(Calendar.MONTH) + 1
    val formattedMonth = if (month < 10) "0$month" else "$month"
    val year = date.get(Calendar.YEAR)
    val week =
        if (oddWeek) stringResource(R.string.odd_week) else stringResource(R.string.even_week)
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$formattedDay.$formattedMonth.$year", textAlign = TextAlign.Center)
        Text(text = if (hasSchedule) week else "", textAlign = TextAlign.Center)
    }
}

@Composable
fun WeekBar(
    selectedDay: WeekDay,
    weekDayToMonthDay: Map<WeekDay, Int>,
    onDayClick: (WeekDay) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        WEEK_DAYS.map { day ->
            Day(
                weekDay = day,
                monthDay = weekDayToMonthDay[day]!!,
                selected = selectedDay == day,
                modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                onClick = { onDayClick(day) }
            )
        }
    }
}

@Composable
fun Day(
    modifier: Modifier = Modifier,
    weekDay: WeekDay,
    monthDay: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            Modifier
                .clickable(
                    onClick = onClick,
                    enabled = true,
                    role = Role.Tab,
                )
                .background(dayBackgroundColor(selected))
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = monthDay.toString(),
                color = dayOfMonthTextColor(selected),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(weekDay.shortNameId),
                style = MaterialTheme.typography.bodySmall,
                color = weekDayTextColor(selected),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun dayBackgroundColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }
}

@Composable
private fun weekDayTextColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colorScheme.inverseOnSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
private fun dayOfMonthTextColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colorScheme.inverseOnSurface
    } else {
        MaterialTheme.colorScheme.onSurface
    }
}

@Preview
@Composable
fun previewSchedulerBar() {
    SchedulerBar(
        selectedDate = Calendar.getInstance(),
        selectedDay = WeekDay.MONDAY,
        weekDayToMonthDay = WEEK_DAYS.mapIndexed { index, weekDay ->
            weekDay to index + 1
        }.toMap(),
        oddWeek = false,
        hasSchedule = true,
        selectDay = {},
        goNextWeek = {},
        goPreviousWeek = {},
        openCalendar = {}
    )
}