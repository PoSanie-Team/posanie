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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.ui.picker.tabColor

enum class WeekDay(val shortName: String) {
    MONDAY("MO"),
    TUESDAY("TU"),
    WEDNESDAY("WE"),
    THURSDAY("TH"),
    FRIDAY("FR"),
    SATURDAY("SA")
}

@Composable
fun SchedulerBar() {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
    ) {
        DateBar()
        WeekBar()
    }
}

@Composable
fun DateBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Localized description"
            )
        }
        Text("22.22.2022")
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Localized description"
            )
        }
    }
}

@Composable
fun WeekBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Day(WeekDay.MONDAY, modifier = Modifier.weight(1f))
        Day(WeekDay.TUESDAY, modifier = Modifier.weight(1f))
        Day(WeekDay.WEDNESDAY, modifier = Modifier.weight(1f))
        Day(WeekDay.THURSDAY, modifier = Modifier.weight(1f))
        Day(WeekDay.FRIDAY, modifier = Modifier.weight(1f))
        Day(WeekDay.SATURDAY, modifier = Modifier.weight(1f))
    }
}

@Composable
fun Day(weekDay: WeekDay, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        Box(
            Modifier
                .clickable(
                    onClick = {  },
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
    SchedulerBar()
}