package dev.timatifey.posanie.ui.groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.ui.GroupsNavItems

@Composable
fun LocalGroupsRoute(
    groupsViewModel: GroupsViewModel,
    navController: NavController
) {
    GroupsRoute(
        groupsViewModel = groupsViewModel,
        navController = navController,
        facultyId = null
    )
}

@Composable
fun FacultyGroupsRoute(
    groupsViewModel: GroupsViewModel,
    navController: NavController,
    facultyId: Long,
) {
    GroupsRoute(
        groupsViewModel = groupsViewModel,
        navController = navController,
        facultyId = facultyId
    )
}

@Composable
private fun GroupsRoute(
    groupsViewModel: GroupsViewModel,
    navController: NavController,
    facultyId: Long?
) {
    val uiState by groupsViewModel.uiState.collectAsState()
    GroupsRoute(
        uiState = uiState,
        isLocal = facultyId == null,
        onGroupPick = { group: Group ->
            groupsViewModel.saveAndPickGroup(group)
            navController.navigate(GroupsNavItems.LocalGroups.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                }
                launchSingleTop = true
            }
        },
        refreshingState = rememberSwipeRefreshState(uiState.isLoading),
        onRefresh = { groupsViewModel.getLocalGroups() },
        onAddGroupButtonClick = {
            navController.navigate(GroupsNavItems.Faculties.route)
        }
    )
}

@Composable
fun FacultiesRoute(
    facultiesViewModel: FacultiesViewModel,
    navController: NavController
) {
    val uiState by facultiesViewModel.uiState.collectAsState()
    FacultiesRoute(
        uiState = uiState,
        onFacultyPick = {
            navController.navigate(GroupsNavItems.FacultyGroups.routeBy(it.id))
        },
        refreshingState = rememberSwipeRefreshState(uiState.isLoading),
        onRefresh = { facultiesViewModel.getFaculties() }
    )
}

@Composable
fun FacultiesRoute(
    uiState: FacultiesUiState,
    onFacultyPick: (Faculty) -> Unit,
    refreshingState: SwipeRefreshState,
    onRefresh: () -> Unit
) {
    when (uiState) {
        is FacultiesUiState.FacultiesList -> FacultiesScreen(
            list = uiState.faculties,
            swipeRefreshState = refreshingState,
            onFacultyPick = onFacultyPick,
            onRefresh = onRefresh
        )
    }
}

@Composable
private fun GroupsRoute(
    uiState: GroupsUiState,
    isLocal: Boolean,
    onGroupPick: (Group) -> Unit,
    refreshingState: SwipeRefreshState,
    onRefresh: () -> Unit,
    onAddGroupButtonClick: () -> Unit
) {
    when (uiState) {
        is GroupsUiState.GroupList -> if (isLocal) LocalGroupsScreen(
            list = uiState.groups,
            swipeRefreshState = refreshingState,
            onGroupClick = onGroupPick,
            onRefresh = onRefresh,
            onAddGroupButtonClick = onAddGroupButtonClick
        ) else PickGroupScreen(
            list = uiState.groups,
            swipeRefreshState = refreshingState,
            onGroupPick = onGroupPick,
            onRefresh = onRefresh
        )
    }
}