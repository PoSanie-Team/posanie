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
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("dev.timatifey.posanie", appContext.packageName)
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun myTest() {
        // Start the app
        composeTestRule.setContent {
            PoSanieTheme(appTheme = AppTheme.DEFAULT, appColorScheme = AppColorScheme.DEFAULT) {
                val viewModel = composeTestRule.activity.viewModels<SettingsViewModel>().value
                SettingsScreen(settingsViewModel = viewModel, recreateActivity = {})
            }
        }

        val themeText = composeTestRule.activity.getText(R.string.theme).toString()
        composeTestRule.onNodeWithText(themeText).assertIsDisplayed()
    }
}