package dev.timatifey.posanie.ui

import android.provider.Contacts.SettingsColumns.KEY
import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Constraints
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
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type
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
                navArgument(KIND_ID_ARG) { type = NavType.LongType },
                navArgument(TYPE_ID_ARG) { type = NavType.StringType }
            )
        ) {
            val facultyId = it.arguments?.getLong(FACULTY_ID_ARG)!!
            val kindId = it.arguments?.getLong(KIND_ID_ARG)!!
            val typeId = it.arguments?.getString(TYPE_ID_ARG)!!

            val facultiesViewModel = hiltViewModel<FacultiesViewModel>()
            val facultyName = facultiesViewModel.getFaculty(facultyId)?.title ?: ""

            val groupsViewModel = hiltViewModel<GroupsViewModel>()

            LaunchedEffect(facultyId, kindId, typeId) {
                groupsViewModel.select(kind = Kind.kindBy(kindId), type = Type.typeBy(typeId))
                groupsViewModel.fetchGroupsBy(facultyId)
            }

            val facultyGroupsState = remember { mutableStateOf(FacultyGroupsState.DEFAULT) }
            FacultyGroupsRoute(
                facultyGroupsState = facultyGroupsState,
                groupsViewModel = groupsViewModel,
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