package dev.timatifey.posanie.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import dev.timatifey.posanie.R

sealed class BottomNavItems(
    val route: String,
    val name: String,
    val icon: Int
) {
    object Scheduler : BottomNavItems("scheduler", "Scheduler", R.drawable.ic_time)
    object Groups : BottomNavItems("groups", "Groups", R.drawable.ic_group)
    object Settings : BottomNavItems("settings", "Settings", R.drawable.ic_settings)
}