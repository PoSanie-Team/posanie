package dev.timatifey.posanie.ui.scheduler

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(viewModel: SchedulerViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val day = uiState.mondayDate.get(Calendar.DAY_OF_MONTH)
    val month = uiState.mondayDate.get(Calendar.MONTH)
    val year = uiState.mondayDate.get(Calendar.YEAR)
    Scaffold(
        topBar = {
            SchedulerBar(
                selectedDate = uiState.selectedDate,
                selectedDay = uiState.selectedDay,
                evenWeek = true,
                selectDay = { day -> viewModel.selectWeekDay(day) },
                goNextWeek = { viewModel.setMonday(year, month, day + 7) },
                goPreviousWeek = { viewModel.setMonday(year, month, day - 7) }
            )
        }
    ) { paddingValues ->
        Text(text = "Schedule", modifier = Modifier.padding(paddingValues))
    }
}