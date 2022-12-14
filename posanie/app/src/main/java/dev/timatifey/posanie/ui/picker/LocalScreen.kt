package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.Teacher

@Composable
fun LocalScreen(
    viewModel: PickerViewModel,
    onGroupClick: (Group) -> Unit,
    onTeacherClick: (Teacher) -> Unit,
    onRefresh: () -> Unit,
    goToRemote: () -> Unit
) {
    val uiState = viewModel.localUiState.collectAsState().value

    val levelsToGroups = uiState.levelsToGroups
    val teachers = uiState.teachers
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading)

    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
            if (!swipeRefreshState.isRefreshing) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp)
                ) {
                    ScheduleTypeTitle(stringResource(R.string.groups))
                    if (levelsToGroups.isEmpty()) {
                        MessageText(
                            text = stringResource(R.string.no_local_groups)
                        )
                    } else {
                        GroupsList(
                            levelsToGroups = levelsToGroups,
                            groupsInRow = 1,
                            onGroupClick = onGroupClick
                        )
                    }
                    ScheduleTypeTitle(stringResource(R.string.teachers))
                    if (teachers.isEmpty()) {
                        MessageText(
                            text = stringResource(R.string.no_local_teachers)
                        )
                    } else {
                        TeachersList(
                            teachersList = teachers,
                            onItemClick = onTeacherClick
                        )
                    }
                }
            }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = goToRemote,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    }
}

@Composable
fun ScheduleTypeTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .fillMaxWidth()
    )
}

@Composable
fun MessageText(
    modifier: Modifier = Modifier
        .padding(4.dp)
        .fillMaxWidth(),
    text: String = ""
) {
    Text(
        text = text,
        modifier = modifier
    )
}