package dev.timatifey.posanie.ui.groups

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.ui.*
import java.lang.IllegalArgumentException

@Composable
fun LocalGroupsRoute(
    groupsViewModel: GroupsViewModel,
    navController: NavHostController
) {
    GroupsRoute(
        groupsViewModel = groupsViewModel,
        navController = navController,
        facultyId = null,
        kindId = null
    )
}

@Composable
fun FacultyGroupsRoute(
    groupsViewModel: GroupsViewModel,
    navController: NavHostController,
    facultyId: Long,
    kindId: Long
) {
    GroupsRoute(
        groupsViewModel = groupsViewModel,
        navController = navController,
        facultyId = facultyId,
        kindId = kindId
    )
}

@Composable
private fun GroupsRoute(
    groupsViewModel: GroupsViewModel,
    navController: NavHostController,
    facultyId: Long?,
    kindId: Long?
) {
    val uiState by groupsViewModel.uiState.collectAsState()
    val isLocal = facultyId == null
    GroupsRoute(
        uiState = uiState,
        isLocal = isLocal,
        listState = groupListStateForKindId(kindId),
        onGroupPick = { group: Group ->
            groupsViewModel.saveAndPickGroup(group)
            navController.navigate(GroupsNavItems.LocalGroups.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        },
        refreshingState = rememberSwipeRefreshState(uiState.isLoading),
        onRefresh = {
            if (isLocal) {
                groupsViewModel.getLocalGroups()
            } else {
                groupsViewModel.fetchGroupsBy(
                    facultyId = facultyId!!,
                    kindId = kindId ?: Kind.defaultKind().id
                )
            }
        },
        onAddGroupButtonClick = {
            navController.navigate(GroupsNavItems.Faculties.route)
        }
    )
}

@Composable
fun groupListStateForKindId(kindId: Long?): LazyListState {
    val localGroupState = rememberLazyListState()
    val facultyGroupStates = mutableMapOf<Long, LazyListState>()
    for (kind in Kind.values()) {
        facultyGroupStates[kind.id] = rememberLazyListState()
    }
    if (kindId != null && !facultyGroupStates.containsKey(kindId)) {
        throw IllegalArgumentException("Incorrect kind id")
    }
    return if (kindId == null) localGroupState else facultyGroupStates[kindId]!!
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
            navController.navigate(GroupsNavItems.FacultyGroups.routeBy(facultyId = it.id))
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
    listState: LazyListState,
    onGroupPick: (Group) -> Unit,
    refreshingState: SwipeRefreshState,
    onRefresh: () -> Unit,
    onAddGroupButtonClick: () -> Unit
) {
    when (uiState) {
        is GroupsUiState.GroupList -> if (isLocal) LocalGroupsScreen(
            list = uiState.groups,
            listState = listState,
            swipeRefreshState = refreshingState,
            onGroupClick = onGroupPick,
            onRefresh = onRefresh,
            onAddGroupButtonClick = onAddGroupButtonClick
        ) else PickGroupScreen(
            list = uiState.groups,
            listState = listState,
            swipeRefreshState = refreshingState,
            onGroupPick = onGroupPick,
            onRefresh = onRefresh
        )
    }
}