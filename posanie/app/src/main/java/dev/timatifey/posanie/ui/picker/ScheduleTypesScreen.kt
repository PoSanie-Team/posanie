package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScheduleTypeScreen(selectGroups: () -> Unit, selectTeachers: () -> Unit) {
    Column {
        ScheduleTypeItem(name = "Groups", onClick = selectGroups)
        ScheduleTypeItem(name = "Teachers", onClick = selectTeachers)
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