package com.care.kmp.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import com.care.kmp.domain.model.Todo
import com.care.kmp.presentation.contract.TodoEffects
import com.care.kmp.presentation.contract.TodoEvents
import com.care.kmp.presentation.view.TodoDetailBottomSheet
import com.care.kmp.presentation.view.TodoItem
import com.care.kmp.presentation.viewmodel.TodoViewModel
import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.ic_add
import kmp.composeapp.generated.resources.ic_delete
import kmp.composeapp.generated.resources.outline_browse_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    backStackEntry: NavBackStackEntry,
    viewModel: TodoViewModel = koinViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Todo) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSchedule: (Todo) -> Unit,
    onNavigateToMap: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val visibleTodos = viewModel.visibleTodos

    val totalCount     = uiState.todos.size
    val completedCount = uiState.todos.count { it.isCompleted }

    var selectedTodo by remember { mutableStateOf<Todo?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Observe result from AddTodo screen
    val todoAdded = backStackEntry.savedStateHandle
        .getStateFlow("todoAdded", false)
        .collectAsState()

    LaunchedEffect(todoAdded.value) {
        if (todoAdded.value) {
            viewModel.sendEvent(TodoEvents.LoadTodos)
            backStackEntry.savedStateHandle["todoAdded"] = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                TodoEffects.NavigateToAddTodo -> {
                    onNavigateToAdd()
                }
                is TodoEffects.NavigateToUpdateTodo -> {
                    onNavigateToEdit(effect.todo)
                }
                is TodoEffects.ShowToast -> {

                }

                TodoEffects.NavigateToSettings ->{
                    onNavigateToSettings()
                }

                is TodoEffects.NavigateToSchedule -> {
                    onNavigateToSchedule(effect.todo)
                }

                is TodoEffects.ShowDetailSheet -> {
                    selectedTodo = effect.todo
                }

                TodoEffects.NavigateToMap -> {
                    onNavigateToMap()
                }
            }
        }
    }

    selectedTodo?.let { todo ->
        TodoDetailBottomSheet(
            todo = todo,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion { selectedTodo = null }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Tasks", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text(
                            "$completedCount / $totalCount completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.sendEvent(TodoEvents.OnClickSettings)
                        }
                    ){
                        Icon(painterResource(Res.drawable.outline_browse_24), contentDescription = "Setting")
                    }
                    IconButton(
                        onClick = {
                            viewModel.sendEvent(TodoEvents.OnClickMap)
                        }
                    ){
                        Icon(Icons.Default.Map, contentDescription = "Setting")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.sendEvent(TodoEvents.AddTodo) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(painterResource(Res.drawable.ic_add), contentDescription = "Add Task")
            }
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Progress bar ─────────────────────────────────────────────────
            if (totalCount > 0) {
                LinearProgressIndicator(
                    progress = { completedCount.toFloat() / totalCount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }

            // ── Filter chips ─────────────────────────────────────────────────
            FilterRow(
                current = uiState.filterCompleted,
                onFilter = {it ->
                    viewModel.sendEvent(TodoEvents.FilterTodos(it))
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // ── List ─────────────────────────────────────────────────────────
            if (visibleTodos.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visibleTodos, key = { it.id }) { todo ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(200)) + expandVertically(),
                            exit  = fadeOut(tween(200)) + shrinkVertically()
                        ) {
                            SwipeToDeleteTodoItem(
                                todo = todo,
                                onToggle = { viewModel.sendEvent(TodoEvents.ToggleTodo(todo.id, !todo.isCompleted)) },
                                onDelete = { viewModel.sendEvent(TodoEvents.DeleteTodo(todo.id)) },
                                onEdit = { viewModel.sendEvent(TodoEvents.UpdateTodo(todo))},
                                onSchedule = { viewModel.sendEvent(TodoEvents.OnClickSchedule(todo)) },
                                onShowDetail = { viewModel.sendEvent(TodoEvents.ShowDetails(todo)) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) } // FAB clearance
                }
            }
        }
    }
}

// ── Filter chips ───────────────────────────────────────────────────────────────

@Composable
private fun FilterRow(
    current: Boolean?,
    onFilter: (Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            "All"       to null,
            "Active"    to false,
            "Done"      to true
        ).forEach { (label, value) ->
            FilterChip(
                selected = current == value,
                onClick  = { onFilter(value) },
                label    = { Text(label) }
            )
        }
    }
}

// ── Swipe-to-delete wrapper ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onSchedule: () -> Unit,
    onShowDetail: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true }
            else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painterResource(Res.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        TodoItem(todo = todo, onToggle = onToggle, onEdit = onEdit, onSchedule = onSchedule,
            onShowDetail = onShowDetail)
    }
}

// ── Empty state ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✅", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "No tasks here!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Tap + to add your first task",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}