package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.ui.BottomNavItems
import dev.timatifey.posanie.utils.ClickListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachersScreen(
    navController: NavHostController,
    viewModel: PickerViewModel,
    searchState: MutableState<SearchState>,
) {
    val uiState = viewModel.teacherSearchUiState.collectAsState().value
    val teachersList = uiState.teachers
    val focusManager = LocalFocusManager.current
    val searchTextState = viewModel.teacherNameSearchState
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading)
    Scaffold(
        topBar = {
            TeachersTopBar(
                navController = navController,
                isDone = searchState.value == SearchState.DONE,
                searchTextState = searchTextState,
                openSearch = { searchState.value = SearchState.IN_PROGRESS },
                submitSearch = {
                    viewModel.fetchTeachersBy(searchTextState.value)
                    searchState.value = SearchState.DONE
                    focusManager.clearFocus()
                },
                closeSearch = {
                    searchState.value = SearchState.NOT_STARTED
                    searchTextState.value = ""
                    viewModel.fetchTeachersBy(searchTextState.value)
                })
        },
        content = { paddingValues ->
            RefreshableTeachersList(
                swipeRefreshState = swipeRefreshState,
                onRefresh = { viewModel.fetchTeachersBy(searchTextState.value) },
                teachersList = teachersList,
                modifier = Modifier.padding(paddingValues),
                clickListener = ClickListener(
                    onClick = {
                        viewModel.saveAndPickTeacher(it)
                        navController.navigate(BottomNavItems.Picker.route)
                    }
                )
            )
        }
    )
}

@Composable
fun RefreshableTeachersList(
    modifier: Modifier = Modifier,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    teachersList: List<Teacher>,
    clickListener: ClickListener<Teacher>,
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        if (!swipeRefreshState.isRefreshing) {
            if (teachersList.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_teachers_found),
                    modifier = Modifier.padding(4.dp)
                )
            } else {
                ScrollableTeachersList(
                    teachersList = teachersList,
                    clickListener = clickListener
                )
            }
        }
    }
}

@Composable
fun ScrollableTeachersList(
    modifier: Modifier = Modifier,
    teachersList: List<Teacher>,
    clickListener: ClickListener<Teacher>,
) {
    LazyColumn(
        modifier = modifier.padding(4.dp)
    ) {
        items(teachersList) { teacher ->
            TeacherItem(teacher = teacher, clickListener = clickListener)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeacherItem(
    teacher: Teacher,
    clickListener: ClickListener<Teacher>
) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        val cardColors = if (teacher.isPicked) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else CardDefaults.cardColors()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .combinedClickable(
                    onClick = { clickListener.onClick(teacher) },
                    onLongClick = { clickListener.onLongClick(teacher) }
                ),
            colors = cardColors,
        ) {
            Text(
                text = teacher.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}