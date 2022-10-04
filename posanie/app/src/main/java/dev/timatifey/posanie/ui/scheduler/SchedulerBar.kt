package dev.timatifey.posanie.ui.scheduler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.ui.picker.tabColor
import java.util.*

enum class WeekDay(val shortName: String) {
    MONDAY("MO"),
    TUESDAY("TU"),
    WEDNESDAY("WE"),
    THURSDAY("TH"),
    FRIDAY("FR"),
    SATURDAY("SA")
}

@Composable
fun SchedulerBar(calendar: Calendar, updateDate: (Int, Int, Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        DateBar(
            calendar,
            goNextWeek = { updateDate(year, month, day + 7)},
            goPreviousWeek = { updateDate(year, month, day - 7)}
        )
        WeekBar(onDayClick = { })
    }
}

@Composable
fun DateBar(calendar: Calendar, goNextWeek: () -> Unit, goPreviousWeek: () -> Unit) {
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
       WeekDate(calendar, true)
        IconButton(onClick = goNextWeek) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Localized description"
            )
        }
    }
}

@Composable
fun WeekDate(calendar: Calendar, evenWeek: Boolean, modifier: Modifier = Modifier) {
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val formattedDay = if (day < 10) "0$day" else "$day"
    val month = calendar.get(Calendar.MONTH) + 1
    val formattedMonth = if (month < 10) "0$month" else "$month"
    val year = calendar.get(Calendar.YEAR)
    val week = if (evenWeek) "even" else "odd"
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text= week, textAlign = TextAlign.Center)
        Text(text = "$formattedDay.$formattedMonth.$year", textAlign = TextAlign.Center)
    }
}

@Composable
fun WeekBar(onDayClick: (WeekDay) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Day(WeekDay.MONDAY, modifier = Modifier.weight(1f)) { onDayClick(WeekDay.MONDAY) }
        Day(WeekDay.TUESDAY, modifier = Modifier.weight(1f)) { onDayClick(WeekDay.TUESDAY) }
        Day(WeekDay.WEDNESDAY, modifier = Modifier.weight(1f)) { onDayClick(WeekDay.WEDNESDAY) }
        Day(WeekDay.THURSDAY, modifier = Modifier.weight(1f)) { onDayClick(WeekDay.THURSDAY) }
        Day(WeekDay.FRIDAY, modifier = Modifier.weight(1f)) { onDayClick(WeekDay.FRIDAY) }
        Day(WeekDay.SATURDAY, modifier = Modifier.weight(1f)) { onDayClick(WeekDay.SATURDAY) }
    }
}

@Composable
fun Day(weekDay: WeekDay, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        Box(
            Modifier
                .clickable(
                    onClick = { },
                    enabled = true,
                    role = Role.Tab,
                )
                .background(dayColor(false))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = weekDay.shortName)
        }
    }
}

@Composable
fun dayColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
}

@Preview
@Composable
fun previewSchedulerBar() {
    SchedulerBar(Calendar.getInstance()) { _, _, _ ->

    }
}