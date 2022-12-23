package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dev.timatifey.posanie.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTypeScreen(
    onBackClick: () -> Unit,
    selectGroups: () -> Unit,
    selectTeachers: () -> Unit
) {
    Scaffold(
        topBar = {
            BasicTopBar(
                onBackClick = onBackClick,
                content = {
                    Text(
                        text = stringResource(R.string.schedule_type_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { paddingValues ->
        Box (modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                ScheduleTypeItem(name = stringResource(R.string.groups), onClick = selectGroups)
                ScheduleTypeItem(name = stringResource(R.string.teachers), onClick = selectTeachers)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTypeItem(name: String, onClick: () -> Unit) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            onClick = onClick,
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}