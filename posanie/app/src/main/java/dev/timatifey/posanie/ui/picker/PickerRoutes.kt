package dev.timatifey.posanie.ui.picker

import android.content.Context
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dev.timatifey.posanie.model.domain.Kind
import dev.timatifey.posanie.model.domain.Type
import dev.timatifey.posanie.ui.*
import kotlinx.coroutines.launch

@Composable
fun LocalRoute(
    navController: NavHostController,
    viewModel: PickerViewModel,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit
) {
    LocalScreen(
        viewModel = viewModel,
        createPopup = createPopup,
        onRefresh = {
            viewModel.viewModelScope.launch {
                viewModel.getLocalGroups()
                viewModel.getLocalTeachers()
            }
        },
        goToRemote = { navController.navigate(PickerNavItems.Remote.route) }
    )
}

@Composable
fun ScheduleTypesRoute(
    navController: NavHostController,
    viewModel: PickerViewModel
) {
    ScheduleTypeScreen(
        viewModel = viewModel,
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

    LaunchedEffect(true) {
        remoteTeachersViewModel.fetchTeachersBy("")
    }

    TeachersScreen(
        navController = navController,
        pickerViewModel = pickerViewModel,
        remoteTeachersViewModel = remoteTeachersViewModel
    )
}

@Composable
fun FacultiesRoute(
    navController: NavHostController,
    facultiesViewModel: FacultiesViewModel
) {
    FacultiesScreen(
        navController = navController,
        facultiesViewModel = facultiesViewModel,
    )
}

@Composable
fun RemoteGroupsRoute(
    context: Context,
    navController: NavHostController,
    pickerViewModel: PickerViewModel,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String
) {
    val remoteGroupsViewModel = hiltViewModel<RemoteGroupsViewModel>()
    LaunchedEffect(true) {
        context.setOnNetworkStateChangeListener { connectionState ->
            if (connectionState == ConnectionState.AVAILABLE) {
                remoteGroupsViewModel.fetchGroupsBy(facultyId)
            }
        }
    }

    LaunchedEffect(facultyId, kindId, typeId) {
        remoteGroupsViewModel.selectFilters(kind = Kind.kindBy(kindId), type = Type.typeBy(typeId))
        remoteGroupsViewModel.fetchGroupsBy(facultyId)
    }

    RemoteGroupsScreen(
        pickerViewModel = pickerViewModel,
        remoteGroupsViewModel = remoteGroupsViewModel,
        navController = navController,
        facultyId = facultyId,
        facultyName = facultyName,
        kindId = kindId,
        typeId = typeId
    )
}
