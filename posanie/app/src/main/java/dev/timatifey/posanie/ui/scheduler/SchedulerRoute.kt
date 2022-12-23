package dev.timatifey.posanie.ui.scheduler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun SchedulerRoute(
    schedulerViewModel: SchedulerViewModel,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit = { _, _ -> }
) {
    SchedulerScreen(schedulerViewModel = schedulerViewModel, createPopup = createPopup)
}

private enum class SchedulerScreenType {
    Main,
    NotPickedGroup
}