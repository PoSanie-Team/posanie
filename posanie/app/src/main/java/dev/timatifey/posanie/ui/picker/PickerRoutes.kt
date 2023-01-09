package dev.timatifey.posanie.ui.picker

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.domain.Kind
import dev.timatifey.posanie.model.domain.Type
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.ui.*

@Composable
fun LocalRoute(
    viewModel: PickerViewModel,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit,
    navController: NavHostController
) {
    LocalScreen(
        viewModel = viewModel,
        createPopup = createPopup,
        onRefresh = {
            viewModel.getLocalGroups()
            viewModel.getLocalTeachers()
        },
        goToRemote = { navController.navigate(PickerNavItems.Remote.route) }
    )
}

@Composable
fun ScheduleTypesRoute(
    navController: NavHostController
) {
    ScheduleTypeScreen(
        onBackClick = { navController.popBackStack() },
        selectGroups = { navController.navigate(RemoteNavItems.Faculties.route) },
        selectTeachers = { navController.navigate(RemoteNavItems.Teachers.route) }
    )
}

@Composable
fun TeachersRoute(
    navController: NavHostController,
    pickerViewModel: PickerViewModel
) {
    val remoteTeachersViewModel = hiltViewModel<RemoteTeachersViewModel>()
    val searchState = remember { mutableStateOf(SearchState.NOT_STARTED) }

    LaunchedEffect(true) {
        remoteTeachersViewModel.fetchTeachersBy("")
    }

    TeachersScreen(
        navController = navController,
        pickerViewModel = pickerViewModel,
        remoteTeachersViewModel = remoteTeachersViewModel,
        searchState = searchState
    )
}

@Composable
fun FacultiesRoute(
    facultiesViewModel: FacultiesViewModel,
    navController: NavHostController
) {
    val uiState by facultiesViewModel.uiState.collectAsState()
    FacultiesRoute(
        navController = navController,
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
    navController: NavHostController,
    uiState: FacultiesUiState,
    onFacultyPick: (Faculty) -> Unit,
    refreshingState: SwipeRefreshState,
    onRefresh: () -> Unit
) {
    when (uiState) {
        is FacultiesUiState.FacultiesList -> FacultiesScreen(
            onBackClick = { navController.popBackStack() },
            list = uiState.faculties,
            swipeRefreshState = refreshingState,
            onFacultyPick = onFacultyPick,
            onRefresh = onRefresh
        )
    }
}
@Composable
fun RemoteGroupsRoute(
    navController: NavHostController,
    pickerViewModel: PickerViewModel,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String
) {
    val remoteGroupsViewModel = hiltViewModel<RemoteGroupsViewModel>()
    val searchState = remember { mutableStateOf(SearchState.NOT_STARTED) }

    LaunchedEffect(facultyId, kindId, typeId) {
        remoteGroupsViewModel.selectFilters(kind = Kind.kindBy(kindId), type = Type.typeBy(typeId))
        remoteGroupsViewModel.fetchGroupsBy(facultyId)
    }

    RemoteGroupsScreen(
        searchState = searchState,
        pickerViewModel = pickerViewModel,
        remoteGroupsViewModel = remoteGroupsViewModel,
        navController = navController,
        facultyId = facultyId,
        facultyName = facultyName,
        kindId = kindId,
        typeId = typeId
    )
}
