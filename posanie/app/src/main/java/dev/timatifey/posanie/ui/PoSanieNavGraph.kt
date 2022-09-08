package dev.timatifey.posanie.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*

import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.timatifey.posanie.ui.groups.*

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
    navController: NavHostController,
    route: String = BottomNavItems.Groups.route,
) {
    navigation(
        startDestination = GroupsNavItems.LocalGroups.route,
        route = route
    ) {
        composable(GroupsNavItems.LocalGroups.route) {
            val viewModel = hiltViewModel<GroupsViewModel>()
            LaunchedEffect(true) {
                viewModel.getLocalGroups()
            }
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
            arguments = listOf(
                navArgument(FACULTY_ID_ARG) { type = NavType.LongType },
                navArgument(KIND_ID_ARG) { type = NavType.LongType }
            )
        ) {
            val viewModel = hiltViewModel<GroupsViewModel>()
            val facultyId = it.arguments?.getLong(FACULTY_ID_ARG)!!
            val kindId = it.arguments?.getLong(KIND_ID_ARG)!!
            LaunchedEffect(facultyId, kindId) {
                viewModel.fetchGroupsBy(facultyId, kindId)
            }
            val degreeNavItems = listOf(
                DegreeNavItems.Bachelor,
                DegreeNavItems.Master,
                DegreeNavItems.Specialist,
                DegreeNavItems.Postgraduate,
            )
            Column {
                GroupKindNavigationBar(
                    navController = navController,
                    facultyId = facultyId,
                    items = degreeNavItems
                )
                FacultyGroupsRoute(
                    groupsViewModel = viewModel,
                    navController = navController,
                    facultyId = facultyId,
                    kindId = kindId
                )
            }

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

@Composable
fun GroupKindNavigationBar(
    navController: NavHostController,
    facultyId: Long,
    items: List<DegreeNavItems>,
    modifier: Modifier = Modifier
) {
    TopNavigationBar(
        modifier = modifier
    ) {
        val viewModel = hiltViewModel<GroupsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val selectedKind = (uiState as GroupsUiState.GroupList).selectedKind
        items.forEach { tab ->
            val tabRoute = GroupsNavItems.FacultyGroups.routeBy(facultyId, tab.kind.id)
            NavigationBarTextItem(
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
fun TopNavigationBar(
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
fun NavigationBarTextItem(
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