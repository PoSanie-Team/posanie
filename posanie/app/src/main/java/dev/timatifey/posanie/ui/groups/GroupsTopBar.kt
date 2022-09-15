package dev.timatifey.posanie.ui.groups

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.focus.onFocusEvent
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


const val COURSE_NAME_LENGTH = 7
const val GROUP_NAME_LENGTH = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
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
            SearchField(
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
fun SearchField(
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