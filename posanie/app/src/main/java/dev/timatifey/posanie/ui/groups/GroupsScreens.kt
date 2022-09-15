package dev.timatifey.posanie.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.cache.COURSE_GROUP_DELIMITER
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel

@Composable
fun LocalGroupsScreen(
    levelsToGroups: Map<Int, GroupsLevel>,
    listState: LazyListState,
    swipeRefreshState: SwipeRefreshState,
    onGroupClick: (Group) -> Unit,
    onRefresh: () -> Unit,
    onAddGroupButtonClick: () -> Unit
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        Column {
            if (!swipeRefreshState.isRefreshing) {
                if (levelsToGroups.isEmpty()) {
                    Text("You have not groups. Please add.")
                } else {
                    GroupsList(
                        levelsToGroups = levelsToGroups,
                        groupsInRow = 1,
                        state = listState,
                        onGroupClick = onGroupClick
                    )
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
    levelsToGroups: Map<Int, GroupsLevel>,
    listState: LazyListState,
    swipeRefreshState: SwipeRefreshState,
    onGroupPick: (Group) -> Unit,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        if (levelsToGroups.isEmpty()) {
            Text("Can't to fetch groups from server.")
        } else if (!swipeRefreshState.isRefreshing) {
            GroupsList(
                levelsToGroups = levelsToGroups,
                groupsInRow = 2,
                state = listState,
                onGroupClick = onGroupPick
            )
        }
    }
}

@Composable
fun GroupsList(levelsToGroups: Map<Int, GroupsLevel>, groupsInRow: Int, state: LazyListState, onGroupClick: (Group) -> Unit) {
    LazyColumn(
        state = state,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
    ) {
        val levels = levelsToGroups.keys.toList().sorted()
        items(levels) { level ->
            Column {
                Text("$level курс")
                GroupsLevelList(levelsToGroups[level]?.getGroups() ?: emptyList(), groupsInRow, onGroupClick)
            }
        }
    }
}

@Composable
fun GroupsLevelList(list: List<Group>, groupsInRow: Int, onGroupClick: (Group) -> Unit) {
    val table = list.toGroupsTable(groupsInRow)
    Column {
        table.forEach { row ->
            GroupsRow(row = row, groupsInRow = groupsInRow, onGroupClick = onGroupClick)
        }
    }
}

private fun List<Group>.toGroupsTable(groupsInRow: Int) : List<List<Group>> {
    val table = mutableListOf<List<Group>>()
    for (i in this.indices step groupsInRow) {
        val row = mutableListOf<Group>()
        for (j in 0 until groupsInRow) {
            if (i + j < this.size) row.add(this[i + j])
            else break
        }
        table.add(row)
    }
    return table
}

@Composable
fun GroupsRow(
    row: List<Group>,
    groupsInRow: Int,
    onGroupClick: (Group) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (group in row) {
            GroupItem(
                group = group,
                twoLines = groupsInRow > 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                onGroupClick = onGroupClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupItem(group: Group, twoLines: Boolean, modifier: Modifier = Modifier, onGroupClick: (Group) -> Unit) {
    Card(
        modifier = modifier,
        onClick = { onGroupClick(group) }
    ) {
        val courseString = group.title.substringBefore(COURSE_GROUP_DELIMITER)
        val groupString = group.title.substringAfter(COURSE_GROUP_DELIMITER)
        val text =
            if (twoLines) courseString + COURSE_GROUP_DELIMITER + '\n' + groupString
            else courseString + COURSE_GROUP_DELIMITER + groupString
        Text(
            text = text,
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FacultiesList(list: List<Faculty>, onFacultyClick: (Faculty) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(4.dp)
    ) {
        items(list) { faculty ->
            FacultyItem(faculty, onFacultyClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyItem(faculty: Faculty, onFacultyClick: (Faculty) -> Unit) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            onClick = { onFacultyClick(faculty) },
        ) {
            Text(
                text = faculty.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun LocalGroupsScreenPreview(
) {
    LocalGroupsScreen(
        levelsToGroups = mapOf(
            1 to GroupsLevel(
                1, mutableListOf(
                    Group(title = "3530901/90202"),
                    Group(title = "3530901/90202123"),
                    Group(title = "3530901/902023424")
                )
            ),
            2 to GroupsLevel(
                2, mutableListOf(
                    Group(title = "3530901/90203"),
                    Group(title = "35309/90201"),
                    Group(title = "35309/90101")
                )
            ),
            3 to GroupsLevel(
                3, mutableListOf(
                    Group(title = "35309/90101")
                )
            ),
        ),
        listState = rememberLazyListState(),
        swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
        {}, {}, {}
    )
}

@Preview
@Composable
fun GroupsListPreview() {
    GroupsLevelList(listOf(
        Group(title = "3530901/90202"),
        Group(title = "3530901/90202123"),
        Group(title = "3530901/902023424"),
        Group(title = "3530901/90203"),
        Group(title = "35309/90201"),
        Group(title = "35309/90101"),
    ), 2,
        {}
    )
}

@Preview
@Composable
fun GroupItemPreview() {
    GroupItem(
        group = Group(
            title = "3530901/90202",
        ),
        twoLines = false,
        onGroupClick = {}
    )
}

@Preview
@Composable
fun FacultyListPreview() {
    FacultiesList(listOf(
        Faculty(title = "Институт Бибы и Бобы"),
        Faculty(title = "Высшая школа Похуй"),
        Faculty(title = "Институт Компьютерных Институт Компьютерных Институт Компьютерных"),
        Faculty(title = "Институт про который все забыли"),
        Faculty(title = "Очень классный гуманитарный институт!!!!!"),
        Faculty(title = "Я устал..."),
    ),
        {}
    )
}

@Preview
@Composable
fun FacultyItemPreview() {
    FacultyItem(
        Faculty(title = "Институт Бибы и Бобы"),
        onFacultyClick = {}
    )
}