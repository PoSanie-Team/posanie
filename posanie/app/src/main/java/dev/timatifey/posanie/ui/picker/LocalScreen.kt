package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.model.domain.Teacher

@Composable
fun LocalScreen(
    levelsToGroups: Map<Int, GroupsLevel>,
    teachers: List<Teacher>,
    swipeRefreshState: SwipeRefreshState,
    onGroupClick: (Group) -> Unit,
    onTeacherClick: (Teacher) -> Unit,
    onRefresh: () -> Unit,
    goToRemote: () -> Unit
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
            if (!swipeRefreshState.isRefreshing) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    ScheduleTypeTitle("Groups")
                    if (levelsToGroups.isEmpty()) {
                        Text(
                            text = "You have not groups. Please add.",
                            modifier = Modifier.padding(4.dp)
                        )
                    } else {
                        GroupsList(
                            levelsToGroups = levelsToGroups,
                            groupsInRow = 1,
                            onGroupClick = onGroupClick
                        )
                    }
                    ScheduleTypeTitle("Teachers")
                    if (teachers.isEmpty()) {
                        Text(
                            text ="You have not teachers. Please add.",
                            modifier = Modifier.padding(4.dp)
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
        modifier = Modifier.padding(PaddingValues(
            horizontal = 4.dp,
            vertical = 4.dp
        ))
    )
}