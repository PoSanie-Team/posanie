package dev.timatifey.posanie

import dev.timatifey.posanie.model.Result
import dev.timatifey.posanie.model.domain.Lesson
import dev.timatifey.posanie.ui.scheduler.WeekDay
import dev.timatifey.posanie.usecases.DayToLessonsMap
import dev.timatifey.posanie.usecases.LessonsUseCase
import java.util.*

class FakeLessonsUseCase: LessonsUseCase {
    override suspend fun getLessonsByGroupId(groupId: Long): Result<DayToLessonsMap> {
        return Result.Success(getFakeGroupLessons())
    }

    override suspend fun getLessonsByTeacherId(teacherId: Long): Result<DayToLessonsMap> {
        return Result.Success(getFakeTeacherLessons())
    }

    override suspend fun saveGroupLessons(
        groupId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap
    ): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun saveTeacherLessons(
        teacherId: Long,
        mondayDate: Calendar,
        weekIsOdd: Boolean,
        dayToLessonsMap: DayToLessonsMap
    ): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun getGroupSchedulerWeekOddness(groupId: Long): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun getTeacherSchedulerWeekOddness(teacherId: Long): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun getGroupSchedulerWeekMonday(groupId: Long): Result<Calendar> {
        val result = Calendar.getInstance()
        result.set(2023, 2, 27)
        return Result.Success(result)
    }

    override suspend fun getTeacherSchedulerWeekMonday(teacherId: Long): Result<Calendar> {
        val result = Calendar.getInstance()
        result.set(2023, 2, 27)
        return Result.Success(result)
    }

    override suspend fun fetchLessonsByGroupId(
        groupId: Long,
        date: String
    ): Result<DayToLessonsMap> {
        return Result.Success(getFakeGroupLessons())
    }

    override suspend fun fetchLessonsByTeacherId(
        teacherId: Long,
        date: String
    ): Result<DayToLessonsMap> {
        return Result.Success(getFakeTeacherLessons())
    }

    override suspend fun fetchWeekOddnessByGroupId(groupId: Long, date: String): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun fetchWeekOddnessByTeacherId(groupId: Long, date: String): Result<Boolean> {
        return Result.Success(true)
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
                lmsUrl = "https://dl.spbstu.ru//course/view.php?id=4744"
            )
        )
        return result
    }
}