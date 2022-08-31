package dev.timatifey.posanie.ui.scheduler

import androidx.compose.runtime.Composable

@Composable
fun SchedulerRoute(
    homeViewModel: SchedulerViewModel,
) {
    SchedulerScreen()
}

private enum class SchedulerScreenType {
    Main,
    NotPickedGroup
}