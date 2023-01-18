package dev.timatifey.posanie.api

import dev.timatifey.posanie.model.data.Lesson
import dev.timatifey.posanie.ui.scheduler.WeekDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup

class LessonsAPI(private val dispatcher: CoroutineDispatcher)  {

    suspend fun weekOddnessByGroupId(groupId: Long, date: String) = withContext(dispatcher) {
        val url = "https://ruz.spbstu.ru/api/v1/ruz/scheduler/$groupId?date=$date"
        var json = Jsoup.connect(url).ignoreContentType(true).execute().body()
        json = json.substring(json.indexOf("{"))
        return@withContext JSONObject(json).getJSONObject("week").getBoolean("is_odd")
    }

    suspend fun weekOddnessByTeacherId(teacherId: Long, date: String) = withContext(dispatcher) {
        val url = "https://ruz.spbstu.ru/api/v1/ruz/teachers/$teacherId/scheduler?date=$date"
        var json = Jsoup.connect(url).ignoreContentType(true).execute().body()
        json = json.substring(json.indexOf("{"))
        return@withContext JSONObject(json).getJSONObject("week").getBoolean("is_odd")
    }

    suspend fun getLessonsByGroupId(groupId: Long, date: String) = withContext(dispatcher) {
        val url =  "https://ruz.spbstu.ru/api/v1/ruz/scheduler/$groupId?date=$date"
        return@withContext getLessons(groupId, url)
    }

    suspend fun getLessonsByTeacherId(teacherId: Long, date: String) = withContext(dispatcher) {
        val url = "https://ruz.spbstu.ru/api/v1/ruz/teachers/$teacherId/scheduler?date=$date"
        return@withContext getLessons(teacherId, url)
    }

    private suspend fun getLessons(schedulerTypeId: Long, url: String) = withContext(dispatcher) {
        val lessonsToDays = mutableMapOf<WeekDay, List<Lesson>>()
        var json = Jsoup.connect(url).ignoreContentType(true).execute().body()
        json = json.substring(json.indexOf("{"))
        val daysArray = JSONObject(json).getJSONArray("days")
        var lessonId = 0L
        for (i in 0 until daysArray.length()) {
            val day = daysArray.get(i) as JSONObject
            val weekDayOrdinal = day.getInt("weekday")
            val weekDay = WeekDay.getWorkDayByOrdinal(adjustWeekDayOrdinal(weekDayOrdinal))
            val lessonsArray = day.getJSONArray("lessons")
            val lessons = mutableListOf<Lesson>()
            for (j in 0 until  lessonsArray.length()) {
                val jsonObject = lessonsArray.optJSONObject(j)
                val teacher = if (jsonObject.getString("teachers") != "null") {
                    val teachers = jsonObject.getJSONArray("teachers")
                    teachers.getJSONObject(0).getString("full_name")
                } else ""
                val lesson = Lesson(
                    id = (schedulerTypeId.toString() + lessonId.toString()).toLong(),
                    start = jsonObject.getString("time_start"),
                    end = jsonObject.getString("time_end"),
                    type = jsonObject.getJSONObject("typeObj").getString("name"),
                    name = jsonObject.getString("subject"),
                    place = getPlace(jsonObject),
                    teacher = teacher,
                    lmsUrl = jsonObject.getString("lms_url")
                )
                lessons.add(lesson)
                lessonId++
            }

            lessonsToDays[weekDay] = lessons;
        }
        return@withContext lessonsToDays
    }

    private fun getPlace(jsonObject: JSONObject): String {
        val auditory = jsonObject.getJSONArray("auditories").get(0) as JSONObject
        val auditoryNumber = auditory.getString("name")
        val buildingName = auditory.getJSONObject("building").getString("name");
        return "$buildingName, $auditoryNumber"
    }

    private fun adjustWeekDayOrdinal (weekDayOrdinal: Int): Int {
        var result = weekDayOrdinal + 1
        if (result > 7) {
            result = 1
        }
        return result
    }
}