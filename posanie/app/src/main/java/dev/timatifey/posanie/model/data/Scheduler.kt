package dev.timatifey.posanie.model.data

import com.google.gson.annotations.SerializedName
import dev.timatifey.posanie.model.domain.Lesson

data class Scheduler(
    @SerializedName("week")
    val weekData: SchedulerWeek = SchedulerWeek(),
    @SerializedName("days")
    val daysData: List<SchedulerDay> = listOf(),
)

data class SchedulerWeek(
    @SerializedName("date_start")
    val dateStart: String = "",
    @SerializedName("date_end")
    val dateEnd: String = "",
    @SerializedName("is_odd")
    val isOdd: Boolean = false
)

data class SchedulerDay(
    @SerializedName("weekday")
    val weekday: Int = 0,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("lessons")
    val lessonsData: List<SchedulerLesson> = listOf()
)

data class SchedulerLesson(
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("subject_short")
    val subjectShort: String = "",
    @SerializedName("type")
    val type: Int = 0,
    @SerializedName("additional_info")
    val additionalInfo: String = "",
    @SerializedName("time_start")
    val timeStart: String = "",
    @SerializedName("time_end")
    val timeEnd: String = "",
    @SerializedName("parity")
    val parity: Int = 0,
    @SerializedName("typeObj")
    val typeObj: SchedulerLessonType = SchedulerLessonType(),
    @SerializedName("teachers")
    val teachersList: List<SchedulerTeacher>? = listOf(),
    @SerializedName("auditories")
    val auditoriesList: List<SchedulerAuditorium>? = listOf(),
    @SerializedName("webinar_url")
    val webinarUrl: String = "",
    @SerializedName("lms_url")
    val lmsUrl: String = "",
)

fun SchedulerLesson.toLesson(id: Long) = Lesson(
    id = id,
    start = timeStart,
    end = timeEnd,
    name = subject,
    type = typeObj.name,
    place = getAuditorium(),
    teacher = getTeacher(),
    lmsUrl = lmsUrl
)

fun SchedulerLesson.getAuditorium(): String {
    auditoriesList?.let {
        if (auditoriesList.isNotEmpty()) {
            return auditoriesList.first().getFullName()
        }
    }
    return "Не знаю где"
}

fun SchedulerLesson.getTeacher(): String {
    teachersList?.let {
        if (teachersList.isNotEmpty()) {
            return teachersList.first().fullName
        }
    }
    return "Не знаю кто"
}

data class SchedulerLessonType(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("abbr")
    val abbr: String = ""
)

data class SchedulerTeacher(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("oid")
    val oid: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("chair")
    val chair: String = ""
)

data class SchedulerAuditorium(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("building")
    val building: SchedulerAuditoriumBuilding = SchedulerAuditoriumBuilding()
)

fun SchedulerAuditorium.getFullName(): String = "${building.name}, $name"

data class SchedulerAuditoriumBuilding(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("abbr")
    val abbr: String = "",
    @SerializedName("address")
    val address: String = ""
)
