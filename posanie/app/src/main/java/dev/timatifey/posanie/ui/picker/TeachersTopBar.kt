package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
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
    SmallTopAppBar(
        title = {
            TeachersSearchField(
                state = searchTextState,
                textStyle = MaterialTheme.typography.bodyMedium,
                innerPadding = PaddingValues(6.dp),
                onSelected = openSearch,
                submitSearch = submitSearch,
                focusRequester = focusRequester
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
            if (isDone) {
                IconButton (onClick = closeSearch) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Localized description"
                    )
                }
            } else {
                IconButton (onClick = submitSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
    )
}

@Composable
fun TeachersSearchField(
    state: MutableState<String>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    focusRequester: FocusRequester,
    onSelected: () -> Unit,
    submitSearch: () -> Unit
) {
    BasicTextField(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(innerPadding)
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onSelected()
                }
            },

        value = state.value,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { submitSearch() }
        ),
        onValueChange = {
            state.value = it
        },
    )
}