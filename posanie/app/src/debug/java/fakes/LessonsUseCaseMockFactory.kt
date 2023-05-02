package fakes

import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Lesson
import dev.timatifey.posanie.ui.scheduler.WeekDay
import dev.timatifey.posanie.usecases.DayToLessonsMap
import dev.timatifey.posanie.usecases.LessonsUseCase
import org.mockito.ArgumentMatchers.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.*

object LessonsUseCaseMockFactory {

    fun create(): LessonsUseCase {
        val lessonsUseCaseMock = mock<LessonsUseCase> {
            onBlocking { getLessonsByGroupId(anyLong()) } doReturn Result.Success(
                getFakeGroupLessons()
            )
            onBlocking { getLessonsByTeacherId(anyLong()) } doReturn Result.Success(
                getFakeTeacherLessons()
            )
            onBlocking { saveGroupLessons(anyLong(), any(), anyBoolean(), any()) } doReturn Result.Success(true)
            onBlocking { saveTeacherLessons(anyLong(), any(), anyBoolean(), any()) } doReturn Result.Success(true)
            onBlocking { getGroupSchedulerWeekOddness(anyLong()) } doReturn Result.Success(true)
            onBlocking { getTeacherSchedulerWeekOddness(anyLong()) } doReturn Result.Success(true)
            onBlocking { getGroupSchedulerWeekMonday(anyLong()) } doReturn Result.Success(
                getFakeMondayDate()
            )
            onBlocking { getTeacherSchedulerWeekMonday(anyLong()) } doReturn Result.Success(
                getFakeMondayDate()
            )
            onBlocking { fetchLessonsByGroupId(anyLong(), anyString()) } doReturn Result.Success(
                getFakeGroupLessons()
            )
            onBlocking { fetchLessonsByTeacherId(anyLong(), anyString()) } doReturn Result.Success(
                getFakeTeacherLessons()
            )
            onBlocking { fetchWeekOddnessByGroupId(anyLong(), anyString()) } doReturn Result.Success(true)
            onBlocking { fetchWeekOddnessByTeacherId(anyLong(), anyString()) } doReturn Result.Success(true)
        }

        return lessonsUseCaseMock
    }

    private fun getFakeMondayDate() : Calendar {
        val result = Calendar.getInstance()
        result.set(2023, 2, 27)
        return result
    }

    private fun getFakeGroupLessons(): DayToLessonsMap {
        val result = mutableMapOf<WeekDay, List<Lesson>>()
        var currentId = 0L
        result[WeekDay.MONDAY] = listOf(
            Lesson(
                id = currentId++,
                start = "12:00",
                end = "13:40",
                name = "Тестирование программного обеспечения",
                type = "Лекции",
                place = "3-й учебный корпус, 401",
                teacher = "Ерошкин Александр Владимирович",
                groupNames = listOf("3530901/90101, 3530901/90201, 35309091/90202, 3530901/90203"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            )
        )
        result[WeekDay.TUESDAY] = listOf(
            Lesson(
                id = currentId++,
                start = "10:00",
                end = "11:40",
                name = "Разработка сетевых приложений",
                type = "Лабораторные",
                place = "3-й учебный корпус, 303",
                teacher = "",
                groupNames = listOf("3530901/90202"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            ),
            Lesson(
                id = currentId++,
                start = "13:00",
                end = "15:40",
                name = "Разработка сетевых приложений",
                type = "Лабораторные",
                place = "3-й учебный корпус, 402",
                teacher = "Новопашенный Андрей Гелиевич",
                groupNames = listOf("3530901/90202"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            )
        )
        result[WeekDay.WEDNESDAY] = listOf(
            Lesson(
                id = currentId++,
                start = "8:30",
                end = "17:20",
                name = "Военная подготовка",
                type = "Практика",
                place = "Военная кафедра",
                teacher = "",
                groupNames = listOf("3530901/90101, 3530901/90201, 35309091/90202, 3530901/90203"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            )
        )
        result[WeekDay.THURSDAY] = listOf(
            Lesson(
                id = currentId++,
                start = "14:00",
                end = "15:40",
                name = "Системный анализ и принятие решений",
                type = "Лекции",
                place = "11-й учебный корпус, 143",
                teacher = "Сиднев Александр Георгиевич",
                groupNames = listOf("3530901/90101, 3530901/90201, 35309091/90202, 3530901/90203"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            ),
            Lesson(
                id = currentId++,
                start = "16:00",
                end = "17:40",
                name = "Защита информации",
                type = "Лекции",
                place = "3-й учебный корпус, 401",
                teacher = "Новопашенный Андрей Гелиевич",
                groupNames = listOf("3530901/90101, 3530901/90201, 35309091/90202, 3530901/90203"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            )
        )
        result[WeekDay.FRIDAY] = emptyList()
        result[WeekDay.SATURDAY] = listOf(
            Lesson(
                id = currentId++,
                start = "12:00",
                end = "14:40",
                name = "Системный анализ и принятие решений",
                type = "Практика",
                place = "3-й учебный корпус, 401",
                teacher = "Сабонис Сергей Станиславович",
                groupNames = listOf("3530901/90202"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            )
        )
        return result
    }

    private fun getFakeTeacherLessons(): DayToLessonsMap {
        val result = mutableMapOf<WeekDay, List<Lesson>>()
        var currentId = 0L
        result[WeekDay.SATURDAY] = listOf(
            Lesson(
                id = currentId++,
                start = "09:00",
                end = "11:40",
                name = "Системный анализ и принятие решений",
                type = "Практика",
                place = "3-й учебный корпус, 401",
                teacher = "Сабонис Сергей Станиславович",
                groupNames = listOf("35309091/90202"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            ),
            Lesson(
                id = currentId++,
                start = "12:00",
                end = "14:40",
                name = "Системный анализ и принятие решений",
                type = "Практика",
                place = "3-й учебный корпус, 401",
                teacher = "Сабонис Сергей Станиславович",
                groupNames = listOf("35309091/90202"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            ),
            Lesson(
                id = currentId++,
                start = "15:00",
                end = "16:40",
                name = "Системный анализ и принятие решений",
                type = "Практика",
                place = "3-й учебный корпус, 401",
                teacher = "Сабонис Сергей Станиславович",
                groupNames = listOf("35309091/90202"),
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            )
        )
        return result
    }
}
