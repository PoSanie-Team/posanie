package dev.timatifey.posanie.ui.groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.data.Kind
import dev.timatifey.posanie.model.data.Type
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.ui.*
import java.lang.IllegalArgumentException


enum class FacultyGroupsState {
    DEFAULT, SEARCH_IN_PROGRESS, SEARCH_DONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyGroupsRoute(
    facultyGroupsState: MutableState<FacultyGroupsState>,
    groupsViewModel: GroupsViewModel,
    navController: NavHostController,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String
    ) {
    val courseSearchState = groupsViewModel.courseSearchState
    val groupSearchState = groupsViewModel.groupSearchState

    Scaffold(
        topBar = {
            Crossfade(targetState = facultyGroupsState.value) { state ->
                when (state) {
                    FacultyGroupsState.DEFAULT -> {
                        DefaultTopAppBar(
                            navController = navController,
                            facultyName = facultyName,
                            openSearch = { facultyGroupsState.value = FacultyGroupsState.SEARCH_IN_PROGRESS }
                        )
                    }
                    else -> {
                        SearchTopAppBar(
                            navController = navController,
                            typeId = typeId,
                            inProgress = facultyGroupsState.value == FacultyGroupsState.SEARCH_IN_PROGRESS,
                            courseSearchState = courseSearchState,
                            groupSearchState = groupSearchState,
                            openSearch = {
                                facultyGroupsState.value = FacultyGroupsState.SEARCH_IN_PROGRESS
                            },
                            updateSearch = {
                                groupsViewModel.filterGroups()
                            },
                            submitSearch = {
                                facultyGroupsState.value = FacultyGroupsState.SEARCH_DONE
                            },
                            closeSearch = {
                                facultyGroupsState.value = FacultyGroupsState.DEFAULT
                                courseSearchState.value = ""
                                groupSearchState.value = ""
                                groupsViewModel.filterGroups()
                                navController.navigate(GroupsNavItems.FacultyGroups.routeBy(facultyId, kindId)) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }

            }
        },
        content = { innerPadding ->
            FacultyGroupsContent(
                innerPadding = innerPadding,
                navController = navController,
                viewModel = groupsViewModel,
                facultyId = facultyId,
                kindId = kindId,
                typeId = typeId,
                showTypes = facultyGroupsState.value != FacultyGroupsState.DEFAULT
            )
        }
    )
}

@Composable
fun FacultyGroupsContent(
    innerPadding: PaddingValues,
    navController: NavHostController,
    viewModel: GroupsViewModel,
    showTypes: Boolean,
    facultyId: Long,
    kindId: Long,
    typeId: String
) {
    val kindNavItems = listOf(
        KindNavItems.Bachelor,
        KindNavItems.Master,
        KindNavItems.Specialist,
        KindNavItems.Postgraduate,
    )
    val typeNavItems = listOf(
        TypeNavItems.Common,
        TypeNavItems.Evening,
        TypeNavItems.Distance
    )
    Column(
        Modifier.padding(innerPadding)
    ) {
        GroupKindNavigationBar(
            navController = navController,
            facultyId = facultyId,
            items = kindNavItems
        )
        AnimatedVisibility (
            visible = showTypes,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically()
        ) {
            GroupTypeNavigationBar(
                navController = navController,
                facultyId = facultyId,
                kindId = kindId,
                items = typeNavItems
            )
        }
        GroupsContent(
            groupsViewModel = viewModel,
            navController = navController,
            facultyId = facultyId,
            kindId = kindId,
            typeId = typeId
        )
    }
}

@Composable
fun LocalGroupsRoute(
    groupsViewModel: GroupsViewModel,
    navController: NavHostController
) {
    GroupsContent(
        groupsViewModel = groupsViewModel,
        navController = navController,
        facultyId = null,
        kindId = null,
        typeId = null
    )
}

@Composable
fun GroupKindNavigationBar(
    navController: NavHostController,
    facultyId: Long,
    items: List<KindNavItems>,
    modifier: Modifier = Modifier
) {
    CategoryNavigationBar(
        modifier = modifier
    ) {
        val viewModel = hiltViewModel<GroupsViewModel>()
        val uiState by viewModel.searchUiState.collectAsState()
        val selectedKind = uiState.selectedKind
        items.forEach { tab ->
            val tabRoute = GroupsNavItems.FacultyGroups.routeBy(facultyId, tab.kind.id)
            CategoryBarTextItem(
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
fun GroupTypeNavigationBar(
    navController: NavHostController,
    facultyId: Long,
    kindId: Long,
    items: List<TypeNavItems>,
    modifier: Modifier = Modifier
) {
    CategoryNavigationBar(
        modifier = modifier
    ) {
        val viewModel = hiltViewModel<GroupsViewModel>()
        val uiState by viewModel.searchUiState.collectAsState()
        val selectedType = uiState.selectedType
        items.forEach { tab ->
            val tabRoute = if (selectedType != tab.type) {
                GroupsNavItems.FacultyGroups.routeBy(facultyId, kindId, tab.type.id)
            } else {
                GroupsNavItems.FacultyGroups.routeBy(facultyId, kindId, Type.DEFAULT.id)
            }
            CategoryBarTextItem(
                text = stringResource(tab.nameId),
                selected = selectedType == tab.type,
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
fun CategoryNavigationBar(
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
fun CategoryBarTextItem(
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

@Composable
private fun GroupsContent(
    groupsViewModel: GroupsViewModel,
    navController: NavHostController,
    facultyId: Long?,
    kindId: Long?,
    typeId: String?
) {
    val isLocal = facultyId == null
    val uiState = if (isLocal) {
        groupsViewModel.localUiState.collectAsState().value
    } else {
        groupsViewModel.searchUiState.collectAsState().value
    }
    GroupsContent(
        uiState = uiState,
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
                groupsViewModel.select(
                    kind = Kind.kindBy(kindId ?: Kind.DEFAULT.id),
                    type = Type.typeBy(typeId ?: Type.DEFAULT.id)
                )
                groupsViewModel.fetchGroupsBy(facultyId!!)
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
private fun GroupsContent(
    uiState: GroupsUiState,
    listState: LazyListState,
    onGroupPick: (Group) -> Unit,
    refreshingState: SwipeRefreshState,
    onRefresh: () -> Unit,
    onAddGroupButtonClick: () -> Unit
) {
    when (uiState) {
        is GroupsUiState.LocalGroupList -> {
            LocalGroupsScreen(
                levelsToGroups = uiState.groups,
                listState = listState,
                swipeRefreshState = refreshingState,
                onGroupClick = onGroupPick,
                onRefresh = onRefresh,
                onAddGroupButtonClick = onAddGroupButtonClick
            )
        }
        is GroupsUiState.SearchGroupList -> {
            PickGroupScreen(
                levelsToGroups = uiState.groups,
                listState = listState,
                swipeRefreshState = refreshingState,
                onGroupPick = onGroupPick,
                onRefresh = onRefresh
            )
        }
    }
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