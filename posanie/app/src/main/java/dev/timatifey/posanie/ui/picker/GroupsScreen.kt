package dev.timatifey.posanie.ui.picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.cache.COURSE_GROUP_DELIMITER
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.ui.KindNavItems
import dev.timatifey.posanie.ui.PickerNavItems
import dev.timatifey.posanie.ui.RemoteNavItems
import dev.timatifey.posanie.ui.TypeNavItems
import java.lang.IllegalArgumentException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteGroupsScreen(
    searchState: MutableState<SearchState>,
    groupsViewModel: PickerViewModel,
    navController: NavHostController,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String
) {
    val courseSearchState = groupsViewModel.courseSearchState
    val groupSearchState = groupsViewModel.groupSearchState

    Scaffold(
        topBar = {
            GroupsTopBar(
                navController = navController,
                groupsViewModel = groupsViewModel,
                facultyId = facultyId,
                facultyName = facultyName,
                kindId = kindId,
                typeId = typeId,
                searchState = searchState,
                courseSearchState = courseSearchState,
                groupSearchState = groupSearchState
            )
        },
        content = { innerPadding ->
            RemoteGroupsContent(
                innerPadding = innerPadding,
                navController = navController,
                viewModel = groupsViewModel,
                facultyId = facultyId,
                kindId = kindId,
                typeId = typeId,
                showTypes = searchState.value != SearchState.NOT_STARTED
            )
        }
    )
}

@Composable
fun RemoteGroupsContent(
    innerPadding: PaddingValues,
    navController: NavHostController,
    viewModel: PickerViewModel,
    showTypes: Boolean,
    facultyId: Long,
    kindId: Long,
    typeId: String
) {
    val kindNavItems = listOf(
        KindNavItems.Bachelor,
        KindNavItems.Master,
        KindNavItems.Specialist,
        KindNavItems.Postgraduate,
    )
    val typeNavItems = listOf(
        TypeNavItems.Common,
        TypeNavItems.Evening,
        TypeNavItems.Distance
    )
    Column(
        Modifier.padding(innerPadding)
    ) {
        GroupKindNavigationBar(
            navController = navController,
            facultyId = facultyId,
            items = kindNavItems
        )
        AnimatedVisibility (
            visible = showTypes,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically()
        ) {
            GroupTypeNavigationBar(
                navController = navController,
                facultyId = facultyId,
                kindId = kindId,
                items = typeNavItems
            )
        }
        RemoteGroupsList(
            groupsViewModel = viewModel,
            navController = navController,
            facultyId = facultyId,
            kindId = kindId,
            typeId = typeId
        )
    }
}

@Composable
fun GroupKindNavigationBar(
    navController: NavHostController,
    facultyId: Long,
    items: List<KindNavItems>,
    modifier: Modifier = Modifier
) {
    CategoryNavigationBar(
        modifier = modifier
    ) {
        val viewModel = hiltViewModel<PickerViewModel>()
        val uiState by viewModel.groupSearchUiState.collectAsState()
        val selectedKind = uiState.selectedKind
        items.forEach { tab ->
            val tabRoute = RemoteNavItems.Groups.routeBy(facultyId, tab.kind.id)
            CategoryBarTextItem(
                text = stringResource(tab.nameId),
                selected = selectedKind == tab.kind,
                onClick = {
                    navController.navigate(tabRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun GroupTypeNavigationBar(
    navController: NavHostController,
    facultyId: Long,
    kindId: Long,
    items: List<TypeNavItems>,
    modifier: Modifier = Modifier
) {
    CategoryNavigationBar(
        modifier = modifier
    ) {
        val viewModel = hiltViewModel<PickerViewModel>()
        val uiState by viewModel.groupSearchUiState.collectAsState()
        val selectedType = uiState.selectedType
        items.forEach { tab ->
            val tabRoute = if (selectedType != tab.type) {
                RemoteNavItems.Groups.routeBy(facultyId, kindId, tab.type.id)
            } else {
                RemoteNavItems.Groups.routeBy(facultyId, kindId, Type.DEFAULT.id)
            }
            CategoryBarTextItem(
                text = stringResource(tab.nameId),
                selected = selectedType == tab.type,
                onClick = {
                    navController.navigate(tabRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    content: @Composable (Modifier) -> Unit
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        modifier = modifier.selectableGroup()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            content(Modifier.weight(1f))
        }
    }
}

@Composable
fun CategoryBarTextItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
    ) {
        Box(
            modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Tab,
                )
                .background(tabColor(selected))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun tabColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
}

@Composable
private fun RemoteGroupsList(
    groupsViewModel: PickerViewModel,
    navController: NavHostController,
    facultyId: Long,
    kindId: Long,
    typeId: String
) {
    val uiState = groupsViewModel.groupSearchUiState.collectAsState().value
    RefreshableGroupsList(
        levelsToGroups = uiState.levelsToGroups,
        listState = groupsListStateForKindId(kindId),
        onGroupPick = { group: Group ->
            groupsViewModel.saveAndPickGroup(group)
            navController.navigate(PickerNavItems.Local.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        },
        swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading),
        onRefresh = {
            groupsViewModel.selectFilters(
                kind = Kind.kindBy(kindId),
                type = Type.typeBy(typeId)
            )
            groupsViewModel.fetchGroupsBy(facultyId)
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
    levelsToGroups: Map<Int, GroupsLevel>,
    listState: LazyListState,
    swipeRefreshState: SwipeRefreshState,
    onGroupPick: (Group) -> Unit,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh, modifier = Modifier.fillMaxSize()) {
        if (!swipeRefreshState.isRefreshing) {
            if (levelsToGroups.isEmpty()) {
                Text(
                    text = "Can't to fetch groups from server.",
                    modifier = Modifier.padding(4.dp)
                )
            } else {
                ScrollableGroupsList(
                    levelsToGroups = levelsToGroups,
                    groupsInRow = 2,
                    state = listState,
                    onGroupClick = onGroupPick
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
    onGroupClick: (Group) -> Unit)
{
    LazyColumn(
        state = state,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
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
fun GroupsList(
    levelsToGroups: Map<Int, GroupsLevel>,
    groupsInRow: Int,
    onGroupClick: (Group) -> Unit)
{
    Column {
        val levels = levelsToGroups.keys.toList().sorted()
        levels.forEach { level ->
            Column {
                Text(text = "$level курс", modifier = Modifier.padding(4.dp))
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
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        val cardColors = if (group.isPicked) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else CardDefaults.cardColors()
        Card(
            modifier = modifier,
            colors = cardColors,
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