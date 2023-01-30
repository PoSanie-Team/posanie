package dev.timatifey.posanie.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Looper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat.getSystemService

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*

import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.timatifey.posanie.ui.picker.*

import dev.timatifey.posanie.ui.scheduler.SchedulerRoute
import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.settings.SettingsRoute

@Composable
fun PoSanieNavGraph(
    context: Context,
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
                context.setOnNetworkStateChangeListener { connectionState ->
                    schedulerViewModel.updateConnectionState(connectionState)
                }
                schedulerViewModel.fetchLessons()
            }
            SchedulerRoute(
                context = context,
                schedulerViewModel = schedulerViewModel,
                createPopup = createPopup
            )
        }
        pickerNavGraph(
            context = context,
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

internal fun Context.setOnNetworkStateChangeListener(
    onNetworkStateChange: (ConnectionState) -> Unit
) {
    val connectivityManager = getSystemService(this, ConnectivityManager::class.java)
        ?: return
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
    connectivityManager.registerNetworkCallback(
        networkRequest,
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNetworkStateChange(ConnectionState.AVAILABLE)
            }

            override fun onLost(network: Network) {
                onNetworkStateChange(ConnectionState.UNAVAILABLE)
            }
        })
}

private fun NavGraphBuilder.pickerNavGraph(
    context: Context,
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
                pickerViewModel.getLocalGroupsAndTeachers()
            }
            LocalRoute(
                viewModel = pickerViewModel,
                createPopup = createPopup,
                navController = navController,
            )
        }
        remoteNavGraph(
            context = context,
            navController = navController,
            pickerViewModel = pickerViewModel,
            route = PickerNavItems.Remote.route
        )
    }
}

private fun NavGraphBuilder.remoteNavGraph(
    context: Context,
    navController: NavHostController,
    pickerViewModel: PickerViewModel,
    route: String = PickerNavItems.Remote.route
) {
    navigation(
        startDestination = RemoteNavItems.ScheduleTypes.route,
        route = route
    ) {
        composable(RemoteNavItems.ScheduleTypes.route) {
            ScheduleTypesRoute(
                navController = navController,
                viewModel = pickerViewModel
            )
        }
        composable(RemoteNavItems.Teachers.route) {
            TeachersRoute(
                navController = navController,
                pickerViewModel = pickerViewModel
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

            RemoteGroupsRoute(
                context = context,
                navController = navController,
                pickerViewModel = pickerViewModel,
                facultyId = facultyId,
                facultyName = facultyName,
                kindId = kindId,
                typeId = typeId
            )
        }
    }
}

@Composable
internal fun BottomNavigationBar(
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