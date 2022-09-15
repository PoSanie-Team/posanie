package dev.timatifey.posanie.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.data.Kind

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
const val KIND_ID_ARG = "degreeId"

sealed class GroupsNavItems(
    val route: String
) {
    object LocalGroups : GroupsNavItems("local_groups")
    object Faculties : GroupsNavItems("faculties")
    object FacultyGroups : GroupsNavItems("faculty_groups/{$FACULTY_ID_ARG}/{$KIND_ID_ARG}") {
        fun routeBy(facultyId: Long, kindId: Long = Kind.DEFAULT_KIND.id) = "faculty_groups/$facultyId/$kindId"
    }
}

sealed class DegreeNavItems(
    val kind: Kind,
    @StringRes val nameId: Int,
) {
    object Bachelor : DegreeNavItems(Kind.BACHELOR, R.string.bachelor)
    object Master : DegreeNavItems(Kind.MASTER, R.string.master)
    object Specialist : DegreeNavItems(Kind.SPECIALIST, R.string.specialist)
    object Postgraduate : DegreeNavItems(Kind.POSTGRADUATE, R.string.postgraduate)
}
