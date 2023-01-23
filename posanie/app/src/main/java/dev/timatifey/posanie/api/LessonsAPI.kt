package dev.timatifey.posanie.api

import dev.timatifey.posanie.api.Constants.Companion.BASE_URL
import dev.timatifey.posanie.model.data.Lesson
import dev.timatifey.posanie.ui.scheduler.WeekDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LessonsAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun isWeekOddByGroupId(groupId: Long, date: String): Boolean =
        withContext(dispatcher) {
            val url = "$BASE_URL/scheduler/$groupId?date=$date"
            return@withContext url
                .getJsonObjectIgnoringContentType()
                .getJSONObject("week")
                .getBoolean("is_odd")
        }

    suspend fun isWeekOddByTeacherId(teacherId: Long, date: String): Boolean =
        withContext(dispatcher) {
            val url = "$BASE_URL/teachers/$teacherId/scheduler?date=$date"
            return@withContext url
                .getJsonObjectIgnoringContentType()
                .getJSONObject("week")
                .getBoolean("is_odd")
        }

    suspend fun getLessonsByGroupId(groupId: Long, date: String): Map<WeekDay, List<Lesson>> =
        withContext(dispatcher) {
            val url = "$BASE_URL/scheduler/$groupId?date=$date"
            return@withContext getLessonsBy(groupId, url)
        }

    suspend fun getLessonsByTeacherId(teacherId: Long, date: String): Map<WeekDay, List<Lesson>> =
        withContext(dispatcher) {
            val url = "$BASE_URL/teachers/$teacherId/scheduler?date=$date"
            return@withContext getLessonsBy(teacherId, url)
        }

    private fun getLessonsBy(schedulerTypeId: Long, url: String): Map<WeekDay, List<Lesson>> {
        val lessonsToDays = mutableMapOf<WeekDay, List<Lesson>>()
        val daysArray = url.getJsonObjectIgnoringContentType().getJSONArray("days")
        var lessonId = 0L
        for (dayIndex in 0 until daysArray.length()) {
            val dayObj = daysArray.get(dayIndex) as JSONObject
            val lessonsArray = dayObj.getJSONArray("lessons")
            val weekDay = WeekDay.getWorkDayByOrdinal(
                dayObj.getInt("weekday").formatWeekDayOrdinal()
            )
            lessonsToDays[weekDay] = (0 until lessonsArray.length()).map { lessonIndex ->
                lessonsArray
                    .optJSONObject(lessonIndex)
                    .toLesson(lessonId = "$schedulerTypeId${lessonId++}".toLong())
            }
        }
        return lessonsToDays
    }

    private fun JSONObject.toLesson(lessonId: Long): Lesson {
        val teacherFullName = if (!isNull("teachers")) {
            getJSONArray("teachers")
                .getJSONObject(0)
                .getString("full_name")
        } else ""
        return Lesson(
            id = lessonId,
            start = getString("time_start"),
            end = getString("time_end"),
            type = getJSONObject("typeObj").getString("name"),
            name = getString("subject"),
            place = getPlace(),
            teacher = teacherFullName,
            lmsUrl = getString("lms_url")
        )
    }

    private fun JSONObject.getPlace(): String {
        val auditory = getJSONArray("auditories").get(0) as JSONObject
        val auditoryNumber = auditory.getString("name")
        val buildingName = auditory.getJSONObject("building").getString("name")
        return "$buildingName, $auditoryNumber"
    }

    private fun Int.formatWeekDayOrdinal() = this % 7 + 1
}


