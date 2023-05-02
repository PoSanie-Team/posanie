package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.cache.COURSE_GROUP_DELIMITER
import dev.timatifey.posanie.model.domain.Kind
import dev.timatifey.posanie.model.domain.Type
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.ui.PickerNavItems
import dev.timatifey.posanie.utils.ClickListener
import dev.timatifey.posanie.utils.ErrorMessage
import java.lang.IllegalArgumentException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteGroupsScreen(
    pickerViewModel: PickerViewModel,
    remoteGroupsViewModel: RemoteGroupsViewModel,
    navController: NavHostController,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String
) {
    val searchState = remoteGroupsViewModel.searchState
    val courseSearchState = remoteGroupsViewModel.courseSearchState
    val groupSearchState = remoteGroupsViewModel.groupSearchState

    Scaffold(
        topBar = {
            GroupsTopBar(
                navController = navController,
                remoteGroupsViewModel = remoteGroupsViewModel,
                facultyId = facultyId,
                facultyName = facultyName,
                showTypes = searchState.value != SearchState.NOT_STARTED,
                kindId = kindId,
                typeId = typeId,
                searchState = searchState,
                courseSearchState = courseSearchState,
                groupSearchState = groupSearchState
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                RemoteGroupsList(
                    navController = navController,
                    pickerViewModel = pickerViewModel,
                    remoteGroupsViewModel = remoteGroupsViewModel,
                    facultyId = facultyId,
                    kindId = kindId,
                    typeId = typeId,
                )
            }
        }
    )
}

@Composable
private fun RemoteGroupsList(
    pickerViewModel: PickerViewModel,
    remoteGroupsViewModel: RemoteGroupsViewModel,
    navController: NavHostController,
    facultyId: Long,
    kindId: Long,
    typeId: String
) {
    val uiState = remoteGroupsViewModel.uiState.collectAsState().value
    val clickListener = ClickListener(
        onClick = { group: Group ->
            pickerViewModel.saveAndPickGroup(group)
            navController.navigate(PickerNavItems.Local.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
    RefreshableGroupsList(
        errorMessages = uiState.errorMessages,
        levelsToGroups = uiState.levelsToGroups,
        listState = groupsListStateForKindId(kindId),
        clickListener = clickListener,
        swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading),
        onRefresh = {
            remoteGroupsViewModel.selectFilters(
                kind = Kind.kindBy(kindId),
                type = Type.typeBy(typeId)
            )
            remoteGroupsViewModel.fetchGroupsBy(facultyId)
        }
    )
}

@Composable
fun groupsListStateForKindId(kindId: Long): LazyListState {
    val facultyGroupStates = mutableMapOf<Long, LazyListState>()
    for (kind in Kind.values()) {
        facultyGroupStates[kind.id] = rememberLazyListState()
    }
    if (!facultyGroupStates.containsKey(kindId)) {
        throw IllegalArgumentException("Incorrect kind id")
    }
    return facultyGroupStates[kindId]!!
}

@Composable
fun RefreshableGroupsList(
    errorMessages: List<ErrorMessage>,
    levelsToGroups: Map<Int, GroupsLevel>,
    listState: LazyListState,
    swipeRefreshState: SwipeRefreshState,
    clickListener: ClickListener<Group>,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh, modifier = Modifier.fillMaxSize()) {
        if (!swipeRefreshState.isRefreshing) {
            if (errorMessages.isNotEmpty()) {
                Column {
                    errorMessages.forEach { errorMessage ->
                        Text(
                            text = stringResource(errorMessage.messageId),
                            modifier = Modifier.padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                        )
                    }
                }
            } else if (levelsToGroups.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_groups),
                    modifier = Modifier.padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                )
            } else {
                ScrollableGroupsList(
                    levelsToGroups = levelsToGroups,
                    groupsInRow = 2,
                    state = listState,
                    clickListener = clickListener
                )
            }
        }
    }
}

@Composable
fun ScrollableGroupsList(
    levelsToGroups: Map<Int, GroupsLevel>,
    groupsInRow: Int,
    state: LazyListState = rememberLazyListState(),
    clickListener: ClickListener<Group>
) {
    LazyColumn(
        state = state,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
    ) {
        val levels = levelsToGroups.keys.toList().sorted()
        items(levels) { level ->
            Column {
                GroupsLevelTitle(level = level)
                GroupsLevelList(
                    list = levelsToGroups[level]?.getGroups() ?: emptyList(),
                    groupsInRow = groupsInRow,
                    clickListener = clickListener
                )
            }
        }
    }
}

@Composable
fun GroupsLevelTitle(
    level: Int,
    paddingValues: PaddingValues = PaddingValues(8.dp)
) {
    Text(
        text = stringResource(R.string.level, level),
        modifier = Modifier.padding(paddingValues),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
fun GroupsLevelList(list: List<Group>, groupsInRow: Int, clickListener: ClickListener<Group>) {
    val table = list.toGroupsTable(groupsInRow)
    Column {
        table.forEach { row ->
            GroupsRow(row = row, groupsInRow = groupsInRow, clickListener = clickListener)
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
    clickListener: ClickListener<Group>
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (group in row) {
            GroupItem(
                group = group,
                twoLines = groupsInRow > 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                clickListener = clickListener
            )
        }
        for (i in row.size until groupsInRow) {
            Box(modifier = Modifier.weight(1f).padding(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    modifier: Modifier = Modifier,
    group: Group,
    twoLines: Boolean,
    clickListener: ClickListener<Group>
) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        val cardColors = if (group.isPicked) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface
            )
        } else CardDefaults.cardColors()
        Card(
            modifier = modifier.clip(RoundedCornerShape(16.dp)).combinedClickable(
                onClick = { clickListener.onClick(group) },
                onLongClick = { clickListener.onLongClick(group) }
            ),
            colors = cardColors,
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
        ClickListener()
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
        clickListener = ClickListener()
    )
}
