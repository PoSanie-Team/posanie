package dev.timatifey.posanie.ui.picker

import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.ui.*

@Composable
fun LocalRoute(
    groupsViewModel: GroupsViewModel,
    teachersViewModel: TeachersViewModel,
    navController: NavHostController
) {
    val groupsUiState = groupsViewModel.localUiState.value
    val teachersUiState = teachersViewModel.localUiState.value
    LocalScreen(
        levelsToGroups = groupsUiState.levelsToGroups,
        teachers = teachersUiState.teachers,
        swipeRefreshState = rememberSwipeRefreshState(groupsUiState.isLoading || teachersUiState.isLoading),
        onGroupClick = {},
        onTeacherClick = {},
        onRefresh = {
            groupsViewModel.getLocalGroups()
            teachersViewModel.getLocalTeachers()
        },
        goToRemote = { navController.navigate(PickerNavItems.Remote.route) }
    )
}

@Composable
fun ScheduleTypesRoute(
    navController: NavHostController
) {
    ScheduleTypeScreen(
        selectGroups = { navController.navigate(RemoteNavItems.Faculties.route) },
        selectTeachers = { navController.navigate(RemoteNavItems.Teachers.route) }
    )
}

@Composable
fun TeachersRoute(
    navController: NavHostController,
    viewModel: TeachersViewModel,
    searchState: MutableState<SearchState>
) {
    TeachersScreen(
        navController = navController,
        viewModel = viewModel,
        searchState = searchState
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
            navController.navigate(RemoteNavItems.Groups.routeBy(facultyId = it.id))
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
fun RemoteGroupsRoute(
    searchState: MutableState<SearchState>,
    groupsViewModel: GroupsViewModel,
    navController: NavHostController,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String
) {
    RemoteGroupsScreen(
        searchState = searchState,
        groupsViewModel = groupsViewModel,
        navController = navController,
        facultyId = facultyId,
        facultyName = facultyName,
        kindId = kindId,
        typeId = typeId
    )
}
