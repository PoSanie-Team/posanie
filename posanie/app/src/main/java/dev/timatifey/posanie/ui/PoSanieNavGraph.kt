package dev.timatifey.posanie.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import dev.timatifey.posanie.ui.groups.GroupsRoute
import dev.timatifey.posanie.ui.groups.GroupsViewModel
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
            SchedulerRoute(homeViewModel = viewModel)
        }
        composable(BottomNavItems.Groups.route) {
            val viewModel = hiltViewModel<GroupsViewModel>()
            GroupsRoute(groupsViewModel = viewModel)
        }
        composable(BottomNavItems.Settings.route) {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsRoute(settingsViewModel = viewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItems> = navItems
) {
    bottomBar = {
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { screen ->
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text(stringResource(screen.resourceId)) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}