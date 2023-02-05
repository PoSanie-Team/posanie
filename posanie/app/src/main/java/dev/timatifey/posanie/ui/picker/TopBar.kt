package dev.timatifey.posanie.ui.picker

import android.view.KeyEvent
import androidx.annotation.StringRes
import androidx.compose.animation.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.Type
import dev.timatifey.posanie.ui.KindNavItems
import dev.timatifey.posanie.ui.RemoteNavItems
import dev.timatifey.posanie.ui.TypeNavItems

enum class SearchState {
    NOT_STARTED, IN_PROGRESS, DONE
}

const val COURSE_NAME_LENGTH = 7
const val GROUP_NAME_LENGTH = 5

@Composable
fun GroupsTopBar(
    navController: NavHostController,
    remoteGroupsViewModel: RemoteGroupsViewModel,
    facultyId: Long,
    facultyName: String,
    showTypes: Boolean,
    kindId: Long,
    typeId: String,
    searchState: MutableState<SearchState>,
    courseSearchState: MutableState<String>,
    groupSearchState: MutableState<String>
) {
    Column(
        modifier = Modifier.shadow(elevation = 8.dp)
    ) {
        GroupsTopBarName(
            navController = navController,
            remoteGroupsViewModel = remoteGroupsViewModel,
            facultyId = facultyId,
            facultyName = facultyName,
            kindId = kindId,
            typeId = typeId,
            searchState = searchState,
            courseSearchState = courseSearchState,
            groupSearchState = groupSearchState
        )
        GroupsTopBarCategory(
            navController = navController,
            remoteGroupsViewModel = remoteGroupsViewModel,
            showTypes = showTypes,
            facultyId = facultyId,
            kindId = kindId,
        )
    }
}

@Composable
fun GroupsTopBarName(
    navController: NavHostController,
    remoteGroupsViewModel: RemoteGroupsViewModel,
    facultyId: Long,
    facultyName: String,
    kindId: Long,
    typeId: String,
    searchState: MutableState<SearchState>,
    courseSearchState: MutableState<String>,
    groupSearchState: MutableState<String>
) {
    Box {
        SearchGroupsTopBar(
            navController = navController,
            remoteGroupsViewModel = remoteGroupsViewModel,
            facultyId = facultyId,
            kindId = kindId,
            typeId = typeId,
            searchState = searchState,
            courseSearchState = courseSearchState,
            groupSearchState = groupSearchState
        )
        AnimatedVisibility(
            visible = searchState.value == SearchState.NOT_STARTED,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DefaultGroupsTopBar(
                navController = navController,
                facultyName = facultyName,
                searchState = searchState
            )
        }
    }
}

@Composable
fun GroupsTopBarCategory(
    navController: NavHostController,
    remoteGroupsViewModel: RemoteGroupsViewModel,
    showTypes: Boolean,
    facultyId: Long,
    kindId: Long,
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
    GroupKindNavigationBar(
        navController = navController,
        viewModel = remoteGroupsViewModel,
        facultyId = facultyId,
        items = kindNavItems
    )
    AnimatedVisibility(
        visible = showTypes,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically()
    ) {
        GroupTypeNavigationBar(
            navController = navController,
            viewModel = remoteGroupsViewModel,
            facultyId = facultyId,
            kindId = kindId,
            items = typeNavItems
        )
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
            Text(
                text = facultyName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.arrow_back_description)
                )
            }
        },
        actions = {
            IconButton(onClick = openSearch) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_button_description)
                )
            }
        }
    )
}

@Composable
fun SearchGroupsTopBar(
    navController: NavHostController,
    remoteGroupsViewModel: RemoteGroupsViewModel,
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
            remoteGroupsViewModel.filterGroups()
        },
        submitSearch = {
            searchState.value = SearchState.DONE
        },
        closeSearch = {
            searchState.value = SearchState.NOT_STARTED
            courseSearchState.value = ""
            groupSearchState.value = ""
            remoteGroupsViewModel.filterGroups()
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
                    contentDescription = stringResource(R.string.arrow_back_description)
                )
            }
        },
        actions = {
            if (inProgress) {
                IconButton (onClick = submitSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_button_description)
                    )
                }
            } else {
                IconButton (onClick = closeSearch) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close_search_button_description)
                    )
                }
            }
        }
    )
}

@Composable
fun GroupsSearchField(
    modifier: Modifier = Modifier,
    groupPrefix: String,
    courseTextState: MutableState<String>,
    groupTextState: MutableState<String>,
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
            if (courseTextState.value.length == COURSE_NAME_LENGTH) {
                groupFocusRequester.requestFocus()
            } else {
                courseFocusRequester.requestFocus()
            }
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
                if (courseTextState.value.length == COURSE_NAME_LENGTH) {
                    courseTextState.value = courseTextState.value.substring(startIndex, endIndex)
                }
                courseFocusRequester.requestFocus()
            },
            submitSearch = submitSearch
        )
    }
}

@Composable
fun GroupPartTextField(
    modifier: Modifier = Modifier,
    state: MutableState<String>,
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
            Text(text = exampleText, style = textStyle, modifier = modifier.padding(horizontal = 1.dp))
        }
    ) { measuredWidth ->
        BasicSearchField(
            modifier = modifier,
            innerPadding = innerPadding,
            maxWidth = measuredWidth,
            searchTextState = state,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            ),
            focusRequester = focusRequester,
            onSelected = onSelected,
            submitSearch = submitSearch,
            onKeyEvent = {
                var result = false
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL) {
                    if (state.value.isEmpty()) {
                        onMinLength()
                        result = true
                    }
                }
                result
            },
            changeSideEffects = {
                if (it.length <= maxLength) {
                    onChanged()
                }
                if (it.length == maxLength) {
                    onMaxLength()
                }
            },
            canChange = {
                it.length <= maxLength
            }
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

@Composable
fun TeachersTopBar(
    navController: NavHostController,
    isDone: Boolean,
    searchTextState: MutableState<String>,
    openSearch: () -> Unit,
    submitSearch: () -> Unit,
    closeSearch: () -> Unit
) {
    val focusRequester = FocusRequester()
    BasicTopBar(
        onBackClick = { navController.popBackStack() },
        content = {
            TeachersSearchField(
                searchTextState = searchTextState,
                textStyle = MaterialTheme.typography.bodyMedium,
                innerPadding = PaddingValues(6.dp),
                onSelected = openSearch,
                submitSearch = submitSearch,
                focusRequester = focusRequester
            )
        },
        actions = {
            if (isDone) {
                IconButton (onClick = closeSearch) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close_search_button_description)
                    )
                }
            } else {
                IconButton (onClick = submitSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_button_description)
                    )
                }
            }
        }
    )
}

@Composable
fun TeachersSearchField(
    modifier: Modifier = Modifier,
    searchTextState: MutableState<String>,
    textStyle: TextStyle = TextStyle.Default,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    focusRequester: FocusRequester,
    onSelected: () -> Unit,
    submitSearch: () -> Unit
) {
    BasicSearchField(
        searchTextState = searchTextState,
        modifier = modifier,
        textStyle = textStyle,
        innerPadding = innerPadding,
        focusRequester = focusRequester,
        prompt = stringResource(R.string.teacher_search_prompt),
        onSelected = onSelected,
        submitSearch = submitSearch
    )
}

@Composable
fun FacultiesTopBar(
    navController: NavHostController,
    searchState: SearchState,
    searchTextState: MutableState<String>,
    isDone: Boolean,
    openSearch: () -> Unit,
    updateSearch: () -> Unit,
    submitSearch: () -> Unit,
    closeSearch: () -> Unit
) {
    SearchFacultiesTopBar(
        navController = navController,
        isDone = isDone,
        searchTextState = searchTextState,
        inProgress = searchState == SearchState.IN_PROGRESS,
        openSearch = openSearch,
        updateSearch = updateSearch,
        submitSearch = submitSearch,
        closeSearch = closeSearch
    )

    AnimatedVisibility(
        visible = searchState == SearchState.NOT_STARTED,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        DefaultFacultiesTopBar(
            navController = navController,
            openSearch = openSearch
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultFacultiesTopBar(
    navController: NavHostController,
    openSearch: () -> Unit
) {
    SmallTopAppBar(
        modifier = Modifier.shadow(elevation = 8.dp),
        title = {
            Text(
                text = stringResource(R.string.faculties),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.arrow_back_description)
                )
            }
        },
        actions = {
            IconButton(onClick = openSearch) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_button_description)
                )
            }
        }
    )
}

@Composable
fun SearchFacultiesTopBar(
    navController: NavHostController,
    isDone: Boolean,
    searchTextState: MutableState<String>,
    inProgress: Boolean,
    openSearch: () -> Unit,
    updateSearch: () -> Unit,
    submitSearch: () -> Unit,
    closeSearch: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    BasicTopBar(
        onBackClick = { navController.popBackStack() },
        content = {
            FacultiesSearchField(
                searchTextState = searchTextState,
                textStyle = MaterialTheme.typography.bodyMedium,
                innerPadding = PaddingValues(6.dp),
                inProgress = inProgress,
                onSelected = openSearch,
                onChanged = updateSearch,
                submitSearch = submitSearch,
                focusRequester = focusRequester
            )
        },
        actions = {
            if (isDone) {
                IconButton (onClick = closeSearch) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close_search_button_description)
                    )
                }
            } else {
                IconButton (onClick = submitSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_button_description)
                    )
                }
            }
        }
    )
}

@Composable
fun FacultiesSearchField(
    modifier: Modifier = Modifier,
    searchTextState: MutableState<String>,
    textStyle: TextStyle = TextStyle.Default,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    inProgress: Boolean,
    focusRequester: FocusRequester,
    onSelected: () -> Unit,
    onChanged: () -> Unit,
    submitSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(inProgress) {
        if (inProgress) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    BasicSearchField(
        searchTextState = searchTextState,
        modifier = modifier,
        textStyle = textStyle,
        innerPadding = innerPadding,
        focusRequester = focusRequester,
        prompt = stringResource(R.string.faculty_search_prompt),
        onSelected = onSelected,
        changeSideEffects = { onChanged() },
        submitSearch = submitSearch
    )
}

@Composable
fun BasicSearchField(
    modifier: Modifier = Modifier,
    searchTextState: MutableState<String>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    maxWidth: Dp? = null,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    focusRequester: FocusRequester,
    prompt: String = "",
    onSelected: () -> Unit,
    submitSearch: () -> Unit,
    onKeyEvent: (androidx.compose.ui.input.key.KeyEvent) -> Boolean = { false },
    changeSideEffects: (String) -> Unit = {},
    canChange: (String) -> Boolean = { true }
) {
    val modifierWithBackground = modifier
        .background(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(6.dp)
        )
        .padding(innerPadding)
    val modifierWithWidth = if (maxWidth == null) {
        modifierWithBackground.fillMaxWidth()
    } else {
        modifierWithBackground.width(maxWidth)
    }
    val finalModifier = modifierWithWidth
        .focusRequester(focusRequester)
        .onFocusChanged { focusState ->
            if (focusState.isFocused) {
                onSelected()
            }
        }
        .onKeyEvent {
            onKeyEvent(it)
        }
    BasicTextField(
        modifier = finalModifier,
        value = searchTextState.value,
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSecondaryContainer),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        keyboardActions = KeyboardActions(
            onSearch = { submitSearch() }
        ),
        onValueChange = {
            if (canChange(it)) {
                searchTextState.value = it
                changeSideEffects(it)
            }
        },
    )
    val showPrompt = searchTextState.value.isEmpty()
    AnimatedVisibility(
        visible = showPrompt,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = prompt,
            modifier = modifier.padding(innerPadding),
            style = textStyle,
            color = Color.Unspecified.copy(alpha = 0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopBar(
    onBackClick: () -> Unit,
    content: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    SmallTopAppBar(
        modifier = Modifier.shadow(elevation = 8.dp),
        title = content,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.arrow_back_description)
                )
            }
        },
        actions = actions
    )
}

@Composable
fun TitleTopBar(
    title: String
) {
    BasicTopBar(
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopBar(
    content: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    SmallTopAppBar(
        modifier = Modifier.shadow(elevation = 8.dp),
        title = content,
        actions = actions
    )
}