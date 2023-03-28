package dev.timatifey.posanie

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.ui.scheduler.SchedulerScreen
import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.scheduler.WeekDay
import dev.timatifey.posanie.ui.settings.SettingsScreen
import dev.timatifey.posanie.ui.settings.SettingsViewModel
import dev.timatifey.posanie.ui.theme.PoSanieTheme
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class SchedulerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

    @Test
    fun checkNextDayOnSunday() {
        val sundayDate = createSundayDate()

        val viewModel = createViewModel()
        viewModel.selectDate(sundayDate)
        sleep(500)
        viewModel.selectNextWeekDay()
        sleep(500)

        val selectedDate = viewModel.uiState.value.selectedDate
        assert(selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
        assert(selectedDate.get(Calendar.DAY_OF_MONTH) == sundayDate.get(Calendar.DAY_OF_MONTH) + 1)
    }

    @Test
    fun checkPreviousWeekOnSunday() {
        val sundayDate = createSundayDate()

        val viewModel = createViewModel()
        viewModel.selectDate(sundayDate)
        sleep(500)
        viewModel.setPreviousMonday()
        sleep(500)

        val selectedDate = viewModel.uiState.value.selectedDate
        assert(selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
        assert(selectedDate.get(Calendar.DAY_OF_MONTH) == sundayDate.get(Calendar.DAY_OF_MONTH) - 13)
    }

    @Test
    fun checkNextWeekOnSunday() {
        val sundayDate = createSundayDate()

        val viewModel = createViewModel()
        viewModel.selectDate(sundayDate)
        sleep(500)
        viewModel.setNextMonday()
        sleep(500)

        val selectedDate = viewModel.uiState.value.selectedDate
        assert(selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
        assert(selectedDate.get(Calendar.DAY_OF_MONTH) == sundayDate.get(Calendar.DAY_OF_MONTH) + 1)
    }

    @Test
    fun checkSchedulerScreenOnSunday() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        setContent(appContext)
        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.month_and_year_text_description)
        ).assertIsDisplayed()
    }

    private fun setContent(appContext: Context) {
        composeTestRule.setContent {
            val viewModel = createViewModel()
            PoSanieTheme(appTheme = AppTheme.DEFAULT, appColorScheme = AppColorScheme.DEFAULT) {
                SchedulerScreen(context = appContext, schedulerViewModel = viewModel) { _, _ -> }
            }
        }
    }

    private fun createViewModel() =
        SchedulerViewModel(FakeLessonsUseCase(), FakeGroupsUseCase(), FakeTeacherUseCase())

    private fun createSundayDate(): Calendar {
        val sundayDate = Calendar.getInstance()
        sundayDate.set(2023, 2, 26)
        return sundayDate
    }

}