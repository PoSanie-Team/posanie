package dev.timatifey.posanie.ui.scheduler

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun SchedulerRoute(
    context: Context,
    schedulerViewModel: SchedulerViewModel,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit = { _, _ -> }
) {
    SchedulerScreen(
        context = context,
        schedulerViewModel = schedulerViewModel,
        createPopup = createPopup
    )
}