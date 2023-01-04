package dev.timatifey.posanie.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.*

import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type
import dev.timatifey.posanie.ui.picker.*

import dev.timatifey.posanie.ui.scheduler.SchedulerRoute
import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.settings.SettingsRoute

@Composable
fun PoSanieNavGraph(
    modifier: Modifier = Modifier,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val pickerViewModel = hiltViewModel<PickerViewModel>()
    NavHost(
        navController = navController,
        startDestination = BottomNavItems.Scheduler.route,
        modifier = modifier
    ) {
        composable(BottomNavItems.Scheduler.route) {
            val schedulerViewModel = hiltViewModel<SchedulerViewModel>()
            LaunchedEffect(true) {
                schedulerViewModel.fetchLessons()
            }
            SchedulerRoute(schedulerViewModel = schedulerViewModel, createPopup = createPopup)
        }
        pickerNavGraph(
            navController = navController,
            pickerViewModel = pickerViewModel,
            route = BottomNavItems.Picker.route,
            createPopup = createPopup
        )
        composable(BottomNavItems.Settings.route) {
            SettingsRoute()
        }
    }
}

fun NavGraphBuilder.pickerNavGraph(
    navController: NavHostController,
    pickerViewModel: PickerViewModel,
    route: String = BottomNavItems.Picker.route,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit,
) {
    navigation(
        startDestination = PickerNavItems.Local.route,
        route = route
    ) {
        composable(PickerNavItems.Local.route) {
            LaunchedEffect(true) {
                pickerViewModel.getLocalGroups()
                pickerViewModel.getLocalTeachers()
            }
            LocalRoute(
                viewModel = pickerViewModel,
                createPopup = createPopup,
                navController = navController,
            )
        }
        remoteNavGraph(
            navController = navController,
            pickerViewModel = pickerViewModel,
            route = PickerNavItems.Remote.route
        )
    }
}

fun NavGraphBuilder.remoteNavGraph(
    navController: NavHostController,
    pickerViewModel: PickerViewModel,
    route: String = PickerNavItems.Remote.route
) {
    navigation(
        startDestination = RemoteNavItems.ScheduleTypes.route,
        route = route
    ) {
        composable(RemoteNavItems.ScheduleTypes.route) {
            ScheduleTypesRoute(navController = navController)
        }
        composable(RemoteNavItems.Teachers.route) {
            val searchState = remember { mutableStateOf(SearchState.NOT_STARTED) }
            LaunchedEffect(true) {
                pickerViewModel.fetchTeachersBy("")
            }
            TeachersRoute(
                navController = navController,
                viewModel = pickerViewModel,
                searchState = searchState
            )
        }
        composable(RemoteNavItems.Faculties.route) {
            val facultiesViewModel = hiltViewModel<FacultiesViewModel>()
            FacultiesRoute(
                facultiesViewModel = facultiesViewModel,
                navController = navController
            )
        }
        composable(
            RemoteNavItems.Groups.route,
            arguments = listOf(
                navArgument(FACULTY_ID_ARG) { type = NavType.LongType },
                navArgument(KIND_ID_ARG) { type = NavType.LongType },
                navArgument(TYPE_ID_ARG) { type = NavType.StringType }
            )
        ) {
            val facultyId = it.arguments?.getLong(FACULTY_ID_ARG)!!
            val kindId = it.arguments?.getLong(KIND_ID_ARG)!!
            val typeId = it.arguments?.getString(TYPE_ID_ARG)!!

            val facultiesViewModel = hiltViewModel<FacultiesViewModel>()
            val facultyName = facultiesViewModel.getFaculty(facultyId)?.title ?: ""

            LaunchedEffect(facultyId, kindId, typeId) {
                pickerViewModel.selectFilters(kind = Kind.kindBy(kindId), type = Type.typeBy(typeId))
                pickerViewModel.fetchGroupsBy(facultyId)
            }

            val searchState = remember { mutableStateOf(SearchState.NOT_STARTED) }
            RemoteGroupsRoute(
                searchState = searchState,
                groupsViewModel = pickerViewModel,
                navController = navController,
                facultyId = facultyId,
                facultyName = facultyName,
                kindId = kindId,
                typeId = typeId
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItems>
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconId),
                        contentDescription = null
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}