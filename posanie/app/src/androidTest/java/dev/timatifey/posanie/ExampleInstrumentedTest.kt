package dev.timatifey.posanie

import androidx.activity.viewModels
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.ui.MainActivity
import dev.timatifey.posanie.ui.settings.SettingsScreen
import dev.timatifey.posanie.ui.settings.SettingsViewModel
import dev.timatifey.posanie.ui.theme.PoSanieTheme

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("dev.timatifey.posanie", appContext.packageName)
    }

    @Test
    fun myTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        composeTestRule.setContent {
            val fakeUseCase = FakeSettingsUseCaseImpl(appContext)
            val viewModel = SettingsViewModel(fakeUseCase)
            PoSanieTheme(appTheme = AppTheme.DEFAULT, appColorScheme = AppColorScheme.DEFAULT) {
                SettingsScreen(settingsViewModel = viewModel, recreateActivity = {})
            }
        }

        val themeText = appContext.getString(R.string.theme)
        composeTestRule.onNodeWithText(themeText).assertIsDisplayed()
    }
}