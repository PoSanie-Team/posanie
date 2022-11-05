package dev.timatifey.posanie.ui.picker

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.ui.BottomNavItems

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
                onItemClick = {
                    viewModel.saveAndPickTeacher(it)
                    navController.navigate(BottomNavItems.Picker.route)
                }
            )
        }
    )
}

@Composable
fun RefreshableTeachersList(
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    teachersList: List<Teacher>,
    onItemClick: (Teacher) -> Unit,
    modifier: Modifier
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize().padding(4.dp)
    ) {
        if (!swipeRefreshState.isRefreshing) {
            if (teachersList.isEmpty()) {
                Text(
                    text ="Can't find any teachers. Please try another search query.",
                    modifier = Modifier.padding(4.dp)
                )
            } else {
                ScrollableTeachersList(
                    teachersList = teachersList,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun ScrollableTeachersList(
    teachersList: List<Teacher>,
    modifier: Modifier = Modifier,
    onItemClick: (Teacher) -> Unit,
) {
    LazyColumn(
        modifier = modifier.padding(4.dp)
    ) {
        items(teachersList) { teacher ->
            TeacherItem(teacher = teacher, onClick = onItemClick)
        }
    }
}

@Composable
fun TeachersList(
    teachersList: List<Teacher>,
    modifier: Modifier = Modifier,
    onItemClick: (Teacher) -> Unit,
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        teachersList.forEach { teacher ->
            TeacherItem(teacher = teacher, onClick = onItemClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherItem(teacher: Teacher, onClick: (Teacher) -> Unit) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        val cardColors = if (teacher.isPicked) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else CardDefaults.cardColors()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            colors = cardColors,
            onClick = { onClick(teacher) },
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

@Preview
@Composable
fun TeachersListPreview() {
    TeachersList(listOf(
        Teacher(name = "Гуляка Николай Андреевич"),
        Teacher(name = "Сисюк Наталья Владамировна"),
        Teacher(name = "Заебомбус Ахмед Мустафанович"),
        Teacher(name = "Абоба Николай Викторович"),
        Teacher(name = "Трансплантант Зеро Гелиевич"),
        Teacher(name = "Сус Амог Усович"),
    ), Modifier,
        {}
    )
}