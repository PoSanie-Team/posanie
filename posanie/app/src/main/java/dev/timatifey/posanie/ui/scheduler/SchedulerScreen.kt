package dev.timatifey.posanie.ui.scheduler

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen() {
    Scaffold(
        topBar = { SchedulerBar() }
    ) { paddingValues ->
        Text(text = "Schedule", modifier = Modifier.padding(paddingValues))
    }
}