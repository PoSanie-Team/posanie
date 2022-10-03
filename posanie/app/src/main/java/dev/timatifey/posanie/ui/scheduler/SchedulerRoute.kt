package dev.timatifey.posanie.ui.scheduler

import androidx.compose.runtime.Composable

@Composable
fun SchedulerRoute(
    schedulerViewModel: SchedulerViewModel,
) {
    SchedulerScreen(schedulerViewModel)
}

private enum class SchedulerScreenType {
    Main,
    NotPickedGroup
}