package dev.timatifey.posanie.ui.groups

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.model.domain.Group

@Composable
fun LocalGroupsScreen(
    list: List<Group>,
    swipeRefreshState: SwipeRefreshState,
    onGroupClick: (Group) -> Unit,
    onRefresh: () -> Unit,
    onAddGroupButtonClick: () -> Unit
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        Column {
            if (!swipeRefreshState.isRefreshing) {
                if (list.isEmpty()) {
                    Text("You have not groups. Please add.")
                } else {
                    GroupsList(list, onGroupClick)
                }
            }
            FloatingActionButton(
                onClick = onAddGroupButtonClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }

    }
}


@Composable
fun FacultiesScreen(
    list: List<Faculty>,
    swipeRefreshState: SwipeRefreshState,
    onFacultyPick: (Faculty) -> Unit,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        if (!swipeRefreshState.isRefreshing) {
            if (list.isEmpty()) {
                Text("Can't to fetch faculties from server.")
            } else {
                FacultiesList(list, onFacultyPick)
            }
        }
    }
}


@Composable
fun PickGroupScreen(
    list: List<Group>,
    swipeRefreshState: SwipeRefreshState,
    onGroupPick: (Group) -> Unit,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        if (list.isEmpty()) {
            Text("Can't to fetch groups from server.")
        } else {
            GroupsList(list, onGroupPick)
        }
    }
}

@Composable
fun GroupsList(list: List<Group>, onGroupClick: (Group) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
    ) {
        items(list) { group ->
            GroupItem(group, onGroupClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupItem(group: Group, onGroupClick: (Group) -> Unit) {
    Card(
        onClick = { onGroupClick(group) },
    ) {
        Text(
            text = group.title,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        )
    }
}

@Composable
fun FacultiesList(list: List<Faculty>, onFacultyClick: (Faculty) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(list) { faculty ->
            FacultyItem(faculty, onFacultyClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyItem(faculty: Faculty, onFacultyClick: (Faculty) -> Unit) {
    Card(
        onClick = { onFacultyClick(faculty) },
    ) {
        Text(
            text = faculty.title,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        )
    }
}

@Preview
@Composable
fun LocalGroupsScreenPreview(
) {
    LocalGroupsScreen(
        list = listOf(
            Group(title = "3530901/90202"),
            Group(title = "3530901/90202123"),
            Group(title = "3530901/902023424"),
        ),
        swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
        {}, {}, {}
    )
}

@Preview
@Composable
fun GroupsListPreview() {
    GroupsList(listOf(
        Group(title = "3530901/90202"),
        Group(title = "3530901/90202123"),
        Group(title = "3530901/902023424"),
    ),
        {}
    )
}

@Preview
@Composable
fun GroupItemPreview() {
    GroupItem(
        Group(
            title = "3530901/90202"
        ),
        {}
    )
}