package dev.timatifey.posanie

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.timatifey.posanie.fakes.GroupsUseCaseMockFactory
import dev.timatifey.posanie.fakes.LessonsUseCaseMockFactory
import dev.timatifey.posanie.fakes.TeachersUseCaseMockFactory
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.ui.scheduler.SchedulerScreen
import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.scheduler.WeekDay
import dev.timatifey.posanie.ui.theme.PoSanieTheme
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import java.util.*

@RunWith(AndroidJUnit4::class)
class SchedulerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
        sleep(500)
        val mondayDate = viewModel.uiState.value.mondayDate
        val selectedDate = viewModel.uiState.value.selectedDate

        assertEquals(mondayDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.SATURDAY)
        assertEquals(selectedDate.get(Calendar.DAY_OF_MONTH), sundayDate.get(Calendar.DAY_OF_MONTH) - 1)
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

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.FRIDAY)
        assertEquals(selectedDate.get(Calendar.DAY_OF_MONTH), sundayDate.get(Calendar.DAY_OF_MONTH) - 2)
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

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(selectedDate.get(Calendar.DAY_OF_MONTH), sundayDate.get(Calendar.DAY_OF_MONTH) + 1)
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

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(selectedDate.get(Calendar.DAY_OF_MONTH), sundayDate.get(Calendar.DAY_OF_MONTH) - 13)
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

        assertEquals(selectedDate.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY)
        assertEquals(selectedDate.get(Calendar.DAY_OF_MONTH), sundayDate.get(Calendar.DAY_OF_MONTH) + 1)
    }

    @Test
    fun checkSchedulerScreenOnSunday() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        setContent(appContext)

        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.month_and_year_text_description)
        ).assertIsDisplayed()
    }

    @Test
    fun checkLessonCard() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        setContent(appContext)

        val lessonId = 0
        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.lesson_name_text_description, lessonId)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.lesson_type_text_description, lessonId)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.lesson_place_text_description, lessonId)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.lesson_teacher_text_description, lessonId)
        ).assertIsDisplayed()
    }

    private fun setContent(appContext: Context) {
        composeTestRule.setContent {
            val viewModel = createViewModel()
            val mondayDate = createMondayDate()
            viewModel.selectDate(mondayDate)
            PoSanieTheme(appTheme = AppTheme.DEFAULT, appColorScheme = AppColorScheme.DEFAULT) {
                SchedulerScreen(context = appContext, schedulerViewModel = viewModel) { _, _ -> }
            }
        }
    }

    private fun createViewModel() = SchedulerViewModel(
        LessonsUseCaseMockFactory.create(),
        GroupsUseCaseMockFactory.create(),
        TeachersUseCaseMockFactory.create()
    )

    private fun createSundayDate(): Calendar {
        val sundayDate = Calendar.getInstance()
        sundayDate.set(2023, 2, 26)
        return sundayDate
    }

    private fun createMondayDate(): Calendar {
        val mondayDate = Calendar.getInstance()
        mondayDate.set(2023, 2, 27)
        return mondayDate
    }

}
