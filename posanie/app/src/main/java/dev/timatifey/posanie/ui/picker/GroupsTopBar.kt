package dev.timatifey.posanie.ui.picker

import android.view.KeyEvent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.timatifey.posanie.model.data.Type
import dev.timatifey.posanie.ui.RemoteNavItems

enum class SearchState {
    NOT_STARTED, IN_PROGRESS, DONE
}

const val COURSE_NAME_LENGTH = 7
const val GROUP_NAME_LENGTH = 5

@Composable
fun GroupsTopBar(
    navController: NavHostController,
    groupsViewModel: GroupsViewModel,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String,
    searchState: MutableState<SearchState>,
    courseSearchState: MutableState<String>,
    groupSearchState: MutableState<String>,
) {
    Crossfade(targetState = searchState.value) { state ->
        when (state) {
            SearchState.NOT_STARTED -> {
                DefaultGroupsTopBar(
                    navController = navController,
                    facultyName = facultyName,
                    searchState = searchState
                )
            }
            else -> {
                SearchGroupsTopBar(
                    navController = navController,
                    groupsViewModel = groupsViewModel,
                    facultyId = facultyId,
                    kindId = kindId,
                    typeId = typeId,
                    searchState = searchState,
                    courseSearchState = courseSearchState,
                    groupSearchState = groupSearchState
                )
            }
        }

    }
}

@Composable
fun DefaultGroupsTopBar(
    navController: NavHostController,
    facultyName: String,
    searchState: MutableState<SearchState>
) {
    DefaultGroupsTopBar(
        navController = navController,
        facultyName = facultyName,
        openSearch = { searchState.value = SearchState.IN_PROGRESS }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultGroupsTopBar(
    navController: NavHostController,
    facultyName: String,
    openSearch: () -> Unit
) {
    SmallTopAppBar(
        title = {
            Text(text = facultyName, style = MaterialTheme.typography.titleMedium)
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = openSearch) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}

@Composable
fun SearchGroupsTopBar(
    navController: NavHostController,
    groupsViewModel: GroupsViewModel,
    facultyId: Long,
    kindId: Long,
    typeId: String,
    searchState: MutableState<SearchState>,
    courseSearchState: MutableState<String>,
    groupSearchState: MutableState<String>,
) {
    SearchGroupsTopBar(
        navController = navController,
        typeId = typeId,
        inProgress = searchState.value == SearchState.IN_PROGRESS,
        courseSearchState = courseSearchState,
        groupSearchState = groupSearchState,
        openSearch = {
            searchState.value = SearchState.IN_PROGRESS
        },
        updateSearch = {
            groupsViewModel.filterGroups()
        },
        submitSearch = {
            searchState.value = SearchState.DONE
        },
        closeSearch = {
            searchState.value = SearchState.NOT_STARTED
            courseSearchState.value = ""
            groupSearchState.value = ""
            groupsViewModel.filterGroups()
            navController.navigate(RemoteNavItems.Groups.routeBy(facultyId, kindId)) {
                launchSingleTop = true
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchGroupsTopBar(
    navController: NavHostController,
    typeId: String,
    inProgress: Boolean,
    courseSearchState: MutableState<String>,
    groupSearchState: MutableState<String>,
    openSearch: () -> Unit,
    updateSearch: () -> Unit,
    submitSearch: () -> Unit,
    closeSearch: () -> Unit,
) {
    SmallTopAppBar(
        title = {
            val groupPrefix = Type.typeBy(typeId).prefix
            GroupsSearchField(
                courseTextState = courseSearchState,
                groupTextState = groupSearchState,
                groupPrefix = groupPrefix,
                textStyle = MaterialTheme.typography.bodyMedium,
                inProgress = inProgress,
                onSelected = openSearch,
                onChanged = updateSearch,
                submitSearch = submitSearch
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            if (inProgress) {
                IconButton (onClick = submitSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Localized description"
                    )
                }
            } else {
                IconButton (onClick = closeSearch) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
    )
}

@Composable
fun GroupsSearchField(
    groupPrefix: String,
    courseTextState: MutableState<String>,
    groupTextState: MutableState<String>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    inProgress: Boolean,
    onSelected: () -> Unit,
    onChanged: () -> Unit,
    submitSearch: () -> Unit
) {
    val courseFocusRequester = remember { FocusRequester() }
    val groupFocusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(inProgress) {
        if (inProgress) {
            courseFocusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        val textFieldPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        val textPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)

        Text(
            text = groupPrefix,
            style = textStyle,
            modifier = modifier.padding(textPadding)
        )
        GroupPartTextField(
            state = courseTextState,
            maxLength = COURSE_NAME_LENGTH,
            textStyle = textStyle,
            innerPadding = textFieldPadding,
            focusRequester = courseFocusRequester,
            onSelected = onSelected,
            onChanged = onChanged,
            onMaxLength = {
                groupFocusRequester.requestFocus()
            },
            submitSearch = submitSearch
        )
        Text(
            text = "/",
            style = textStyle,
            modifier = modifier.padding(textPadding)
        )
        GroupPartTextField(
            state = groupTextState,
            maxLength = GROUP_NAME_LENGTH,
            textStyle = textStyle,
            innerPadding = textFieldPadding,
            focusRequester = groupFocusRequester,
            onSelected = onSelected,
            onChanged = onChanged,
            onMinLength = {
                val startIndex = 0
                val endIndex = courseTextState.value.length - 1
                courseTextState.value = courseTextState.value.substring(startIndex, endIndex)
                courseFocusRequester.requestFocus()
            },
            submitSearch = submitSearch
        )
    }
}

@Composable
fun GroupPartTextField(
    state: MutableState<String>,
    modifier: Modifier = Modifier,
    maxLength: Int,
    textStyle: TextStyle = TextStyle.Default,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    focusRequester: FocusRequester,
    onSelected: () -> Unit,
    onChanged: () -> Unit,
    onMaxLength: () -> Unit = {},
    onMinLength: () -> Unit = {},
    submitSearch: () -> Unit
) {
    val exampleText = "0".repeat(maxLength)
    MeasureUnconstrainedViewWidth(
        viewToMeasure = {
            Text(text = exampleText, style = textStyle)
        }
    ) { measuredWidth ->
        BasicTextField(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(innerPadding)
                .width(measuredWidth)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onSelected()
                    }
                }
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL) {
                        if (state.value.isEmpty()) {
                            onMinLength()
                            return@onKeyEvent true
                        }
                    }
                    return@onKeyEvent false
                },

            value = state.value,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { submitSearch() }
            ),
            onValueChange = {
                if (it.length <= maxLength) {
                    state.value = it
                    onChanged()
                }
                if (it.length == maxLength) {
                    onMaxLength()
                }
            },
        )
    }
}

@Composable
fun MeasureUnconstrainedViewWidth(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (measuredWidth: Dp) -> Unit,
) {
    SubcomposeLayout { constraints ->
        val measuredWidth = subcompose("viewToMeasure", viewToMeasure)[0]
            .measure(Constraints()).width.toDp()

        val contentPlaceable = subcompose("content") {
            content(measuredWidth)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}