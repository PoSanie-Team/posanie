package dev.timatifey.posanie.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type

sealed class BottomNavItems(
    val route: String,
    @DrawableRes val iconId: Int
) {
    object Scheduler : BottomNavItems("scheduler", R.drawable.ic_time)
    object Picker : BottomNavItems("picker", R.drawable.ic_group)
    object Settings : BottomNavItems("settings", R.drawable.ic_settings)
}

const val FACULTY_ID_ARG = "facultyId"
const val KIND_ID_ARG = "degreeId"
const val TYPE_ID_ARG = "typeId"

sealed class PickerNavItems(
    val route: String
) {
    object Local : PickerNavItems("local")
    object Remote : PickerNavItems("remote")
}

sealed class RemoteNavItems(
    val route: String
) {
    object ScheduleTypes : RemoteNavItems("schedule_types")
    object Faculties : RemoteNavItems("faculties")
    object Teachers : RemoteNavItems("teachers")
    object Groups : RemoteNavItems("groups/{$FACULTY_ID_ARG}/{$KIND_ID_ARG}/{$TYPE_ID_ARG}") {
        fun routeBy(
            facultyId: Long,
            kindId: Long = Kind.DEFAULT.id,
            typeId: String = Type.DEFAULT.id
        ) = "groups/$facultyId/$kindId/$typeId"
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
