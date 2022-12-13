package dev.timatifey.posanie.ui.settings

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.timatifey.posanie.ui.MainActivity

@Composable
fun SettingsRoute(
    localViewModel: SettingsViewModel
) {
    SettingsScreen(settingsViewModel = localViewModel)
}

@Composable
fun SettingsRoute() {
    val activity = LocalContext.current.getActivity() ?: return
    val globalViewModel: SettingsViewModel by activity.viewModels()
    SettingsScreen(settingsViewModel = globalViewModel)
}

fun Context.getActivity(): MainActivity? = when (this) {
    is MainActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}