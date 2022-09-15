package dev.timatifey.posanie.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type

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
const val TYPE_ID_ARG = "typeId"

sealed class GroupsNavItems(
    val route: String
) {
    object LocalGroups : GroupsNavItems("local_groups")
    object Faculties : GroupsNavItems("faculties")
    object FacultyGroups : GroupsNavItems("faculty_groups/{$FACULTY_ID_ARG}/{$KIND_ID_ARG}/{$TYPE_ID_ARG}") {
        fun routeBy(
            facultyId: Long,
            kindId: Long = Kind.DEFAULT.id,
            typeId: String = Type.DEFAULT.id
        ) = "faculty_groups/$facultyId/$kindId/$typeId"
    }
}

sealed class KindNavItems(
    val kind: Kind,
    @StringRes val nameId: Int,
) {
    object Bachelor : KindNavItems(Kind.BACHELOR, R.string.bachelor)
    object Master : KindNavItems(Kind.MASTER, R.string.master)
    object Specialist : KindNavItems(Kind.SPECIALIST, R.string.specialist)
    object Postgraduate : KindNavItems(Kind.POSTGRADUATE, R.string.postgraduate)
}

sealed class TypeNavItems(
    val type: Type,
    @StringRes val nameId: Int,
) {
    object Common : TypeNavItems(Type.COMMON, R.string.common)
    object Evening : TypeNavItems(Type.EVENING, R.string.evening)
    object Distance : TypeNavItems(Type.DISTANCE, R.string.distance)
}
