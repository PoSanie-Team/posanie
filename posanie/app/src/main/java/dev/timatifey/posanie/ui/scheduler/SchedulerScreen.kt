package dev.timatifey.posanie.ui.scheduler

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.domain.Lesson
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(schedulerViewModel: SchedulerViewModel) {
    val schedulerUiState = schedulerViewModel.uiState.collectAsState().value

    val day = schedulerUiState.mondayDate.get(Calendar.DAY_OF_MONTH)
    val month = schedulerUiState.mondayDate.get(Calendar.MONTH)
    val year = schedulerUiState.mondayDate.get(Calendar.YEAR)

    val lessons = (schedulerUiState as SchedulerUiState.UiState).selectedLessons

    Scaffold(
        topBar = {
            SchedulerBar(
                selectedDate = schedulerUiState.selectedDate,
                selectedDay = schedulerUiState.selectedDay,
                oddWeek = schedulerUiState.weekIsOdd,
                hasSchedule = schedulerUiState.hasSchedule && !schedulerUiState.isLoading,
                selectDay = { day -> schedulerViewModel.selectWeekDay(day) },
                goNextWeek = { schedulerViewModel.setMonday(year, month, day + 7) },
                goPreviousWeek = { schedulerViewModel.setMonday(year, month, day - 7) }
            )
        }
    ) { paddingValues ->
        if (schedulerUiState.hasSchedule) {
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = rememberSwipeRefreshState(schedulerUiState.isLoading),
                onRefresh = {
                    schedulerViewModel.fetchLessons()
                }
            ) {
                if (!schedulerUiState.isLoading) {
                    if (lessons.isEmpty()) {
                        MessageText(text = "No lessons today.")
                    } else {
                        LessonsList(lessons = lessons)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                MessageText(text = "Please choose group or teacher first.")
            }
        }
    }
}

@Composable
fun MessageText(
    modifier: Modifier = Modifier.padding(16.dp).fillMaxWidth(),
    text: String = ""
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
fun LessonsList(modifier: Modifier = Modifier, lessons: List<Lesson>) {
    LazyColumn(modifier = modifier) {
        items(lessons.size) { index -> LessonItem(lesson = lessons[index]) }
    }
}

@Composable
fun LessonItem(modifier: Modifier = Modifier, lesson: Lesson) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = lesson.start, modifier = Modifier.padding(4.dp))
            Text(text = lesson.end, modifier = Modifier.padding(4.dp))
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(4.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(text = lesson.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 4.dp))
                Text(text = lesson.type, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                Text(text = lesson.place, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                Text(text = lesson.teacher, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
            }

        }
    }

}

@Preview
@Composable
fun LessonItemPreview() {
    LessonItem(
        lesson = Lesson(
            id = 0,
            start = "10:00",
            end = "11:40",
            name = "Цифровая обработка сигналов",
            type = "Лабораторные",
            place = "3-й учебный корпус, 401",
            teacher = "Лупин Анатолий Викторович",
            lmsUrl = ""
        )
    )
}

@Preview
@Composable
fun ShortLessonItemPreview() {
    LessonItem(
        lesson = Lesson(
            id = 0,
            start = "10:00",
            end = "11:40",
            name = "Военная подготовка",
            type = "Лабораторные",
            place = "3-й учебный корпус, 401",
            teacher = "Лупин Анатолий Викторович",
            lmsUrl = ""
        )
    )
}
