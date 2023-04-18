package dev.timatifey.posanie

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.ui.scheduler.SchedulerScreen
import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.theme.PoSanieTheme
import fakes.GroupsUseCaseMockFactory
import fakes.LessonsUseCaseMockFactory
import fakes.TeachersUseCaseMockFactory
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class SchedulerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
            appContext.getString(R.string.lesson_card_description, lessonId)
        ).assertIsDisplayed()
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
        composeTestRule.onAllNodesWithContentDescription(
            appContext.getString(R.string.expand_lesson_card_icon_description)
        ).assertCountEquals(3)
    }

    @Test
    fun checkLessonExpandedCard() {
        val appContext =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        setContent(appContext)
        val lessonId = 0

        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.lesson_card_description, lessonId)
        ).performClick()

        composeTestRule.onNodeWithContentDescription(
            appContext.getString(R.string.lesson_group_names_text_description, lessonId)
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

    private fun createMondayDate(): Calendar {
        val mondayDate = Calendar.getInstance()
        mondayDate.set(2023, 2, 27)
        return mondayDate
    }

}
