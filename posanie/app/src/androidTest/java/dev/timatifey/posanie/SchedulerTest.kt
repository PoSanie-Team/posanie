package dev.timatifey.posanie

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.scheduler.WeekDay
import kotlinx.coroutines.delay
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class SchedulerTest {

    @Test
    fun checkWorkDayForSunday() {
        val sundayOrdinal = 1
        val workingWeekDay = WeekDay.getWorkDayByOrdinal(sundayOrdinal)
        assert(workingWeekDay == WeekDay.SATURDAY)
    }

    @Test
    fun checkSunday() {
        val sundayDate = createSundayDate()

        val viewModel = createViewModel()
        viewModel.selectDate(sundayDate)
        sleep(500)

        val mondayDate = viewModel.uiState.value.mondayDate
        assert(mondayDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
        val selectedDate = viewModel.uiState.value.selectedDate
        assert(selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)

        assert(selectedDate.get(Calendar.DAY_OF_MONTH) == sundayDate.get(Calendar.DAY_OF_MONTH) - 1)
    }

    @Test
    fun checkPreviousDayOnSunday() {
        val sundayDate = createSundayDate()

        val viewModel = createViewModel()
        viewModel.selectDate(sundayDate)
        sleep(500)
        viewModel.selectPreviousWeekDay()
        sleep(500)

        val selectedDate = viewModel.uiState.value.selectedDate
        assert(selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
        assert(selectedDate.get(Calendar.DAY_OF_MONTH) == sundayDate.get(Calendar.DAY_OF_MONTH) - 2)
    }

    private fun createViewModel() =
        SchedulerViewModel(FakeLessonsUseCase(), FakeGroupsUseCase(), FakeTeacherUseCase())

    private fun createSundayDate(): Calendar {
        val sundayDate = Calendar.getInstance()
        sundayDate.set(2023, 2, 26)
        return sundayDate
    }

}