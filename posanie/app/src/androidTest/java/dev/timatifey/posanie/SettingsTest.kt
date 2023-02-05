package dev.timatifey.posanie

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.ui.settings.SettingsScreen
import dev.timatifey.posanie.ui.settings.SettingsViewModel
import dev.timatifey.posanie.ui.theme.PoSanieTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkSettingsScreenLabels() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        setContent(appContext)
        composeTestRule.onNodeWithText(appContext.getString(R.string.theme)).assertIsDisplayed()
        composeTestRule.onNodeWithText(appContext.getString(R.string.color_scheme)).assertIsDisplayed()
        composeTestRule.onNodeWithText(appContext.getString(R.string.language)).assertIsDisplayed()
    }

    private fun setContent(appContext: Context) {
        composeTestRule.setContent {
            val fakeUseCase = FakeSettingsUseCaseImpl(appContext)
            val viewModel = SettingsViewModel(fakeUseCase)
            PoSanieTheme(appTheme = AppTheme.DEFAULT, appColorScheme = AppColorScheme.DEFAULT) {
                SettingsScreen(settingsViewModel = viewModel, recreateActivity = {})
            }
        }
    }
}