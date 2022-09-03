package dev.timatifey.posanie.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dev.timatifey.posanie.ui.groups.FacultiesRoute
import dev.timatifey.posanie.ui.groups.FacultiesViewModel

import dev.timatifey.posanie.ui.groups.FacultyGroupsRoute
import dev.timatifey.posanie.ui.groups.GroupsViewModel
import dev.timatifey.posanie.ui.groups.LocalGroupsRoute
import dev.timatifey.posanie.ui.scheduler.SchedulerRoute
import dev.timatifey.posanie.ui.scheduler.SchedulerViewModel
import dev.timatifey.posanie.ui.settings.SettingsRoute
import dev.timatifey.posanie.ui.settings.SettingsViewModel

@Composable
fun PoSanieNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItems.Scheduler.route,
        modifier = modifier
    ) {
        composable(BottomNavItems.Scheduler.route) {
            val viewModel = hiltViewModel<SchedulerViewModel>()
            SchedulerRoute(schedulerViewModel = viewModel)
        }

        groupsNavGraph(
            navController,
            route = BottomNavItems.Groups.route
        )

        composable(BottomNavItems.Settings.route) {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsRoute(settingsViewModel = viewModel)
        }
    }
}

fun NavGraphBuilder.groupsNavGraph(
    navController: NavController,
    route: String = BottomNavItems.Groups.route,
) {
    navigation(
        startDestination = GroupsNavItems.LocalGroups.route,
        route = route
    ) {
        composable(GroupsNavItems.LocalGroups.route) {
            val viewModel = hiltViewModel<GroupsViewModel>()
            LocalGroupsRoute(
                groupsViewModel = viewModel,
                navController = navController
            )
        }
        composable(GroupsNavItems.Faculties.route) {
            val facultiesViewModel = hiltViewModel<FacultiesViewModel>()
            FacultiesRoute(
                facultiesViewModel = facultiesViewModel,
                navController = navController
            )
        }
        composable(
            GroupsNavItems.FacultyGroups.route,
            arguments = listOf(navArgument(FACULTY_ID_ARG) { type = NavType.LongType })
        ) {
            val viewModel = hiltViewModel<GroupsViewModel>()
            val facultyId = it.arguments?.getLong(FACULTY_ID_ARG)!!
            FacultyGroupsRoute(
                groupsViewModel = viewModel,
                navController = navController,
                facultyId = facultyId
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
                label = { Text(stringResource(screen.nameId)) },
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