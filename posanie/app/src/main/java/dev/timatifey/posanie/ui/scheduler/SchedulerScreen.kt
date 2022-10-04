package dev.timatifey.posanie.ui.scheduler

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(viewModel: SchedulerViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    Scaffold(
        topBar = {
            SchedulerBar(calendar = uiState.calendar, updateDate = { year, month, day ->
                viewModel.setDate(year, month, day)
            })
        }
    ) { paddingValues ->
        Text(text = "Schedule", modifier = Modifier.padding(paddingValues))
    }
}