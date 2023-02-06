package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.Faculty
import dev.timatifey.posanie.ui.RemoteNavItems


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultiesScreen(
    navController: NavHostController,
    facultiesViewModel: FacultiesViewModel
) {
    val uiState by facultiesViewModel.uiState.collectAsState()
    val errorMessages = uiState.errorMessages
    val facultyList = uiState.faculties
    val focusManager = LocalFocusManager.current
    val searchState = facultiesViewModel.searchState
    val searchTextState = facultiesViewModel.searchTextState
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading)

    Scaffold(
        topBar = {
            FacultiesTopBar(
                navController = navController,
                isDone = searchState.value == SearchState.DONE,
                searchState = searchState.value,
                searchTextState = searchTextState,
                openSearch = {
                    searchState.value = SearchState.IN_PROGRESS
                },
                updateSearch = {
                    facultiesViewModel.filterFaculties()
                },
                submitSearch = {
                    searchState.value = SearchState.DONE
                    focusManager.clearFocus()
                },
                closeSearch = {
                    searchState.value = SearchState.NOT_STARTED
                    searchTextState.value = ""
                    facultiesViewModel.filterFaculties()
                })
        }
    ) { paddingValues ->
        SwipeRefresh(state = swipeRefreshState, onRefresh = { facultiesViewModel.fetchFaculties() }) {
            if (!swipeRefreshState.isRefreshing) {
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    if (errorMessages.isNotEmpty()) {
                        Column {
                            errorMessages.forEach { errorMessage ->
                                Text(
                                    text = stringResource(errorMessage.messageId),
                                    modifier = Modifier.padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                                )
                            }
                        }
                    } else if (facultyList.isEmpty()) {
                        Text( stringResource(R.string.no_faculties), modifier = Modifier.padding(8.dp))
                    } else {
                        FacultiesList(
                            list = facultyList,
                            onFacultyClick = {
                                navController.navigate(RemoteNavItems.Groups.routeBy(facultyId = it.id))
                            }
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun FacultiesList(list: List<Faculty>, onFacultyClick: (Faculty) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(list) { faculty ->
            FacultyItem(faculty, onFacultyClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyItem(faculty: Faculty, onFacultyClick: (Faculty) -> Unit) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { onFacultyClick(faculty) },
        ) {
            Text(
                text = faculty.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun FacultyListPreview() {
    FacultiesList(listOf(
        Faculty(title = "Институт Бибы и Бобы"),
        Faculty(title = "Высшая школа Неважно"),
        Faculty(title = "Институт Компьютерных Институт Компьютерных Институт Компьютерных"),
        Faculty(title = "Институт про который все забыли"),
        Faculty(title = "Очень классный гуманитарный институт!!!!!"),
        Faculty(title = "Я устал..."),
    ),
        {}
    )
}

@Preview
@Composable
fun FacultyItemPreview() {
    FacultyItem(
        Faculty(title = "Институт Бибы и Бобы"),
        onFacultyClick = {}
    )
}