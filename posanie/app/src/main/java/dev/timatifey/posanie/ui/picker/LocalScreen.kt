package dev.timatifey.posanie.ui.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.timatifey.posanie.R
import dev.timatifey.posanie.model.domain.Group
import dev.timatifey.posanie.model.domain.GroupsLevel
import dev.timatifey.posanie.model.domain.ScheduleType
import dev.timatifey.posanie.model.domain.Teacher
import dev.timatifey.posanie.ui.PopupDialog
import dev.timatifey.posanie.utils.ClickListener

@Composable
fun LocalScreen(
    viewModel: PickerViewModel,
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit,
    onRefresh: () -> Unit,
    goToRemote: () -> Unit
) {
    val uiState by viewModel.localUiState.collectAsState()

    val levelsToGroups = uiState.levelsToGroups
    val teachers = uiState.teachers
    val isLoading = uiState.isLoading
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading)

    val itemDialogVisibilityState = remember { mutableStateOf(false) }
    val itemToDeleteState: MutableState<ScheduleType?> = remember { mutableStateOf(null) }

    val groupClickListener = ClickListener<Group>(
        onClick = { group -> viewModel.pickGroup(group = group) },
        onLongClick = { group ->
            itemDialogVisibilityState.value = true
            itemToDeleteState.value = group
        }
    )
    val teacherClickListener = ClickListener<Teacher>(
        onClick = { teacher -> viewModel.pickTeacher(teacher) },
        onLongClick = { teacher ->
            itemDialogVisibilityState.value = true
            itemToDeleteState.value = teacher
        }
    )

    if (levelsToGroups.isNotEmpty() || teachers.isNotEmpty()) {
        SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
            localGroupsAndTeachers(
                levelsToGroups = levelsToGroups,
                groupClickListener = groupClickListener,
                teachers = teachers,
                teacherClickListener = teacherClickListener
            )
        }
        AddItemFAB(goToRemote = goToRemote)
    }

    LaunchedEffect(levelsToGroups, teachers, isLoading) {
        if (levelsToGroups.isEmpty() && teachers.isEmpty() && !isLoading) {
            goToRemote()
        }
    }

    LaunchedEffect(true) {
        createDeleteItemPopupDialog(
            createPopup = createPopup,
            itemDialogVisibilityState = itemDialogVisibilityState,
            itemToDeleteState = itemToDeleteState,
            deleteGroup = { group -> viewModel.deleteGroup(group = group) },
            deleteTeacher = { teacher -> viewModel.deleteTeacher(teacher = teacher) }
        )
    }
}

fun createDeleteItemPopupDialog(
    createPopup: (MutableState<Boolean>, @Composable () -> Unit) -> Unit,
    itemDialogVisibilityState: MutableState<Boolean>,
    itemToDeleteState: MutableState<ScheduleType?>,
    deleteGroup: (Group) -> Unit,
    deleteTeacher: (Teacher) -> Unit
) {
    createPopup(itemDialogVisibilityState) {
        val itemToDelete = itemToDeleteState.value ?: return@createPopup
        val itemName = when (itemToDelete) {
            is Group -> itemToDelete.title
            is Teacher -> itemToDelete.name
        }
        val itemType = when (itemToDelete) {
            is Group -> stringResource(R.string.groupItemsDescription)
            is Teacher -> stringResource(R.string.teacherItemsDescription)
        }
        PopupDialog(
            title = when (itemToDelete) {
                is Group -> stringResource(R.string.deleteGroupTitle)
                is Teacher -> stringResource(R.string.deleteTeacherTitle)
            },
            modifier = Modifier.width(300.dp).wrapContentHeight(unbounded = true),
            description = stringResource(R.string.deleteItemDescription, itemName, itemType),
            onConfirm = {
                when (itemToDelete) {
                    is Group -> deleteGroup(itemToDelete)
                    is Teacher -> deleteTeacher(itemToDelete)
                }
                itemDialogVisibilityState.value = false
            },
            onCancel = { itemDialogVisibilityState.value = false }
        )
    }
}

@Composable
fun localGroupsAndTeachers(
    levelsToGroups: Map<Int, GroupsLevel>,
    groupClickListener: ClickListener<Group>,
    teachers: List<Teacher>,
    teacherClickListener: ClickListener<Teacher>
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 8.dp)
    ) {
        ContentWithHint(
            contentName = stringResource(R.string.groups),
            needHint = levelsToGroups.isEmpty(),
            hint = stringResource(R.string.no_local_groups),
            content = {
                LocalGroupsList(
                    levelsToGroups = levelsToGroups,
                    groupsInRow = 1,
                    clickListener = groupClickListener
                )
            }
        )
        Spacer(Modifier.padding(8.dp))
        ContentWithHint(
            contentName = stringResource(R.string.teachers),
            needHint = teachers.isEmpty(),
            hint = stringResource(R.string.no_local_teachers),
            content = {
                LocalTeachersList(
                    teachersList = teachers,
                    clickListener = teacherClickListener
                )
            }
        )
    }
}


@Composable
fun ContentWithHint(
    contentName: String,
    needHint: Boolean,
    hint: String,
    content: @Composable () -> Unit
) {
    ScheduleTypeTitle(contentName)
    if (needHint) {
        MessageText(
            Modifier.padding(16.dp),
            text = hint)
    } else {
        Box(Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Composable
fun AddItemFAB(
    goToRemote: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = goToRemote,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = stringResource(R.string.add_item_button_description)
            )
        }
    }
}

@Composable
fun ScheduleTypeTitle(text: String) {
    Card(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
            )
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun LocalTeachersList(
    modifier: Modifier = Modifier,
    teachersList: List<Teacher>,
    clickListener: ClickListener<Teacher>,
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        teachersList.forEach { teacher ->
            TeacherItem(teacher = teacher, clickListener = clickListener)
        }
    }
}

@Composable
fun LocalGroupsList(
    levelsToGroups: Map<Int, GroupsLevel>,
    groupsInRow: Int,
    clickListener: ClickListener<Group>
) {
    Column {
        val levels = levelsToGroups.keys.toList().sorted()
        levels.forEach { level ->
            Column {
                Text(
                    text = stringResource(R.string.level, level),
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
                GroupsLevelList(
                    list = levelsToGroups[level]?.getGroups() ?: emptyList(),
                    groupsInRow = groupsInRow,
                    clickListener = clickListener
                )
            }
        }
    }
}

@Composable
fun MessageText(
    modifier: Modifier = Modifier
        .padding(4.dp)
        .fillMaxWidth(),
    text: String = ""
) {
    Text(
        text = text,
        modifier = modifier
    )
}

@Preview
@Composable
fun LocalTeachersListPreview() {
    LocalTeachersList(
        teachersList = listOf(
            Teacher(name = "Гуляка Николай Андреевич"),
            Teacher(name = "Сисюк Наталья Владамировна"),
            Teacher(name = "Бомбус Ахмед Мустафанович"),
            Teacher(name = "Абоба Николай Викторович"),
            Teacher(name = "Трансплантант Зеро Гелиевич"),
            Teacher(name = "Сус Амог Усович"),
            Teacher(name = "Очень очень очень очень очень очень очень очень очень Длинное Имя")
        ),
        modifier = Modifier,
        clickListener = ClickListener()
    )
}