package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.Faculty


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultiesScreen(
    onBackClick: () -> Unit,
    list: List<Faculty>,
    swipeRefreshState: SwipeRefreshState,
    onFacultyPick: (Faculty) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            BasicTopBar(
                onBackClick = onBackClick,
                content = {
                    Text(
                        text = stringResource(R.string.faculties),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { paddingValues ->
        SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
            if (!swipeRefreshState.isRefreshing) {
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    if (list.isEmpty()) {
                        Text( stringResource(R.string.no_faculties_error), modifier = Modifier.padding(8.dp))
                    } else {
                        FacultiesList(list, onFacultyPick)
                    }
                }
            }
        }
    }

}

@Composable
fun FacultiesList(list: List<Faculty>, onFacultyClick: (Faculty) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                .padding(horizontal = 4.dp, vertical = 4.dp),
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
        Faculty(title = "Высшая школа Похуй"),
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