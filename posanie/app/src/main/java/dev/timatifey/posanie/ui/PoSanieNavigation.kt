package dev.timatifey.posanie.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.timatifey.posanie.R

sealed class BottomNavItems(
    val route: String,
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int
) {
    object Scheduler : BottomNavItems("scheduler", R.string.scheduler, R.drawable.ic_time)
    object Groups : BottomNavItems("groups", R.string.groups, R.drawable.ic_group)
    object Settings : BottomNavItems("settings", R.string.settings, R.drawable.ic_settings)
}

const val FACULTY_ID_ARG = "facultyId"

sealed class GroupsNavItems(
    val route: String
) {
    object LocalGroups : GroupsNavItems("local_groups")
    object Faculties : GroupsNavItems("faculties")
    object FacultyGroups : GroupsNavItems("faculty_groups/{$FACULTY_ID_ARG}") {
        fun routeBy(facultyId: Long) = "faculty_groups/$facultyId"
    }
}
