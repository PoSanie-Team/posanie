package dev.timatifey.posanie.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.data.Language

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    val uiState = settingsViewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        SettingsTitle()
        ThemeSettings(
            checked = uiState.darkTheme,
            setDarkTheme = { settingsViewModel.saveAndPickTheme(it) }
        )
        LanguageSettings(
            selectedLanguage = uiState.language,
            onLanguageClick = { settingsViewModel.saveAndPickLanguage(it) }
        )
    }
}

@Composable
fun SettingsTitle() {
    Text(
        text = stringResource(R.string.settings),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ThemeSettings(
    checked: Boolean,
    setDarkTheme: (Boolean) -> Unit
) {
    Text(
        text = stringResource(R.string.theme),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
    SettingsSwitchOption(optionName = stringResource(R.string.dark_theme), checked = checked, onCheckChange = setDarkTheme)
}

@Composable
fun SettingsSwitchOption(
    modifier: Modifier = Modifier.padding(4.dp),
    optionName: String,
    checked: Boolean = false,
    onCheckChange: (Boolean) -> Unit
) {
    SettingsOption(modifier = modifier, optionName = optionName) {
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckChange(!checked)
            })
    }
}

@Composable
fun LanguageSettings(
    selectedLanguage: Language,
    onLanguageClick: (Language) -> Unit
) {
    Text(
        text = stringResource(R.string.language),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
    LanguageOption(
        language = Language.ENGLISH,
        selected = selectedLanguage == Language.ENGLISH,
        onClick = { onLanguageClick(Language.ENGLISH) }
    )
    LanguageOption(
        language = Language.RUSSIAN,
        selected = selectedLanguage == Language.RUSSIAN,
        onClick = { onLanguageClick(Language.RUSSIAN) }
    )
}

@Composable
fun LanguageOption(
    modifier: Modifier = Modifier.padding(horizontal = 4.dp),
    language: Language,
    selected: Boolean,
    onClick: () -> Unit
) {
    SettingsOption(modifier = modifier, optionName = language.englishName) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

@Composable
fun SettingsOption(modifier: Modifier = Modifier, optionName: String, selector: @Composable () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = optionName)
        selector()
    }
}