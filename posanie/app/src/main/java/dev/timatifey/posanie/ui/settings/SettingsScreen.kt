package dev.timatifey.posanie.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.BuildConfig
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme
import dev.timatifey.posanie.model.domain.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    recreateActivity: () -> Unit
) {
    val uiState = settingsViewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopBar(title = stringResource(R.string.settings_title))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.padding(4.dp))
            ThemeSettings(
                selectedTheme = uiState.theme,
                onThemeClick = { settingsViewModel.saveAndPickTheme(it) }
            )
            Spacer(Modifier.padding(4.dp))
            ColorSchemeSettings(
                selectedColorScheme = uiState.colorScheme,
                onColorSchemeClick = { settingsViewModel.saveAndPickColorScheme(it) }
            )
            Spacer(Modifier.padding(4.dp))
            LanguageSettings(
                selectedLanguage = uiState.language,
                onLanguageClick = {
                    settingsViewModel.saveAndPickLanguage(it)
                    recreateActivity()
                }
            )
            Spacer(Modifier.padding(16.dp))
            AppVersionLabel()
        }
    }
}

@Composable
fun ThemeSettings(
    selectedTheme: AppTheme,
    onThemeClick: (AppTheme) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsTitle(stringResource(R.string.theme))
        for (theme in AppTheme.values()) {
            ThemeOption(
                theme = theme,
                selected = selectedTheme == theme,
                onClick = { onThemeClick(theme) }
            )
        }
    }
}

@Composable
fun ThemeOption(
    theme: AppTheme,
    selected: Boolean,
    onClick: () -> Unit
) {
    SettingsOption(
        optionDescription = {
            Text(text = stringResource(theme.nameId))
        },
        selector = {
            RadioButton(selected = selected, onClick = onClick)
        }
    )
}

@Composable
fun ColorSchemeSettings(
    selectedColorScheme: AppColorScheme,
    onColorSchemeClick: (AppColorScheme) -> Unit
) {
    Column {
        SettingsTitle(stringResource(R.string.color_scheme))
        for (colorScheme in AppColorScheme.values()) {
            ColorSchemeOption(
                colorScheme = colorScheme,
                selected = selectedColorScheme == colorScheme,
                onClick = { onColorSchemeClick(colorScheme) }
            )
        }
    }
}

@Composable
fun ColorSchemeOption(
    colorScheme: AppColorScheme,
    selected: Boolean,
    onClick: () -> Unit
) {
    SettingsOption(
        optionDescription = {
            Text(text = stringResource(colorScheme.nameId))
        },
        selector = {
            RadioButton(selected = selected, onClick = onClick)
        }
    )
}

@Composable
fun LanguageSettings(
    selectedLanguage: Language,
    onLanguageClick: (Language) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsTitle(stringResource(R.string.language))
        for (language in Language.values()) {
            LanguageOption(
                language = language,
                selected = selectedLanguage == language,
                onClick = { onLanguageClick(language) }
            )
        }
    }
}

@Composable
fun LanguageOption(
    language: Language,
    selected: Boolean,
    onClick: () -> Unit
) {
    SettingsOption(
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
    optionDescription: @Composable () -> Unit,
    selector: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            optionDescription()
        }
        selector()
    }
}

@Composable
fun SettingsTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String
) {
    SmallTopAppBar(
        modifier = Modifier.shadow(elevation = 8.dp),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    )
}

@Composable
fun AppVersionLabel() {
    val appName = stringResource(R.string.app_name)
    val versionName = BuildConfig.VERSION_NAME
    val text = "$appName: $versionName"
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}