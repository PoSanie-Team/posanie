package dev.timatifey.posanie

import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.scheduler.WeekDay
import fakes.GroupsUseCaseMockFactory
import fakes.LessonsUseCaseMockFactory
import fakes.TeachersUseCaseMockFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.*

class SchedulerTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun checkWorkDayForSunday() {
        val sundayOrdinal = 1

        val workingWeekDay = WeekDay.getWorkDayByOrdinal(sundayOrdinal)

        assertEquals(workingWeekDay, WeekDay.SATURDAY)
    }

    @Test
    fun checkSunday() {
        val sundayDate = createSundayDate()
        val viewModel = createViewModel()

        viewModel.selectDate(sundayDate)
        val mondayDate = viewModel.uiState.value.mondayDate
        val selectedDate = viewModel.uiState.value.selectedDate

        assertEquals(mondayDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.SATURDAY)
        assertEquals(
            selectedDate.get(Calendar.DAY_OF_MONTH),
            sundayDate.get(Calendar.DAY_OF_MONTH) - 1
        )
    }

    @Test
    fun checkPreviousDayOnSunday() {
        val sundayDate = createSundayDate()
        val viewModel = createViewModel()

        viewModel.selectDate(sundayDate)
        viewModel.selectPreviousWeekDay()
        val selectedDate = viewModel.uiState.value.selectedDate

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.FRIDAY)
        assertEquals(
            selectedDate.get(Calendar.DAY_OF_MONTH),
            sundayDate.get(Calendar.DAY_OF_MONTH) - 2
        )
    }

    @Test
    fun checkNextDayOnSunday() {
        val sundayDate = createSundayDate()
        val viewModel = createViewModel()

        viewModel.selectDate(sundayDate)
        viewModel.selectNextWeekDay()
        val selectedDate = viewModel.uiState.value.selectedDate

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(
            selectedDate.get(Calendar.DAY_OF_MONTH),
            sundayDate.get(Calendar.DAY_OF_MONTH) + 1
        )
    }

    @Test
    fun checkPreviousWeekOnSunday() {
        val sundayDate = createSundayDate()
        val viewModel = createViewModel()

        viewModel.selectDate(sundayDate)
        viewModel.setPreviousMonday()
        val selectedDate = viewModel.uiState.value.selectedDate

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(
            selectedDate.get(Calendar.DAY_OF_MONTH),
            sundayDate.get(Calendar.DAY_OF_MONTH) - 13
        )
    }

    @Test
    fun checkNextWeekOnSunday() {
        val sundayDate = createSundayDate()
        val viewModel = createViewModel()

        viewModel.selectDate(sundayDate)
        viewModel.setNextMonday()
        val selectedDate = viewModel.uiState.value.selectedDate

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(
            selectedDate.get(Calendar.DAY_OF_MONTH),
            sundayDate.get(Calendar.DAY_OF_MONTH) + 1
        )
    }

    private fun createSundayDate(): Calendar {
        val sundayDate = Calendar.getInstance()
        sundayDate.set(2023, 2, 26)
        return sundayDate
    }

    private fun createViewModel() = SchedulerViewModel(
        LessonsUseCaseMockFactory.create(),
        GroupsUseCaseMockFactory.create(),
        TeachersUseCaseMockFactory.create()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    class MainDispatcherRule(
        private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

}
