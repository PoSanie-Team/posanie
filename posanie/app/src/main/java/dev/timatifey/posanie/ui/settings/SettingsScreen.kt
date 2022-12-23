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
    settingsViewModel: SettingsViewModel,
    recreateActivity: () -> Unit
) {
    val uiState = settingsViewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        ThemeSettings(
            checked = uiState.darkTheme,
            setDarkTheme = { settingsViewModel.saveAndPickTheme(it) }
        )
        LanguageSettings(
            selectedLanguage = uiState.language,
            onLanguageClick = {
                settingsViewModel.saveAndPickLanguage(it)
                recreateActivity()
            }
        )
    }
}

@Composable
fun ThemeSettings(
    checked: Boolean,
    setDarkTheme: (Boolean) -> Unit
) {
    Text(
        text = stringResource(R.string.theme),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
    SettingsSwitchOption(optionName = stringResource(R.string.dark_theme), checked = checked, onCheckChange = setDarkTheme)
}

@Composable
fun SettingsSwitchOption(
    modifier: Modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
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
        style = MaterialTheme.typography.titleLarge,
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
    modifier: Modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
    language: Language,
    selected: Boolean,
    onClick: () -> Unit
) {
    SettingsOption(
        modifier = modifier,
        optionDescription = {
            Column {
                Text(text = language.originName)
                Text(text = language.englishName, style = MaterialTheme.typography.bodySmall)
            }
        },
        selector = {
            RadioButton(selected = selected, onClick = onClick)
        }
    )
}

@Composable
fun SettingsOption(
    modifier: Modifier = Modifier,
    optionName: String,
    selector: @Composable () -> Unit
) {
    SettingsOption(
        modifier = modifier,
        optionDescription = { Text(text = optionName) },
        selector = selector
    )
}

@Composable
fun SettingsOption(
    modifier: Modifier = Modifier,
    optionDescription: @Composable () -> Unit,
    selector: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        optionDescription()
        selector()
    }
}