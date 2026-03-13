package com.care.schedule.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*
import com.care.schedule.domain.model.TodoModel
import com.care.schedule.presentation.viewmodel.ScheduleViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

// ScheduleScreen.kt
@Composable
fun ScheduleScreen(
    todo: TodoModel,
    onNavigateBack: () -> Unit,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var selectedSecond by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Schedule task", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // task card
        TaskCard(todo = todo)

        Spacer(modifier = Modifier.height(16.dp))

        // time picker + countdown
        TimerCard(
            selectedHour = selectedHour,
            selectedMinute = selectedMinute,
            selectedSecond = selectedSecond,
            remainingTime = uiState.remainingTime,
            onHourChange = { selectedHour = it },
            onMinuteChange = { selectedMinute = it },
            onSecondChange = { selectedSecond = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // status
        if (uiState.isScheduled) {
            ScheduledStatusBanner()
            Spacer(modifier = Modifier.height(12.dp))
        }

        // buttons
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { viewModel.onCancelSchedule() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val scheduledMillis = calculateScheduledTime(
                        selectedHour, selectedMinute, selectedSecond
                    )
                    viewModel.onScheduleTask(
                        todo = todo,
                        scheduledTimeMillis = scheduledMillis,
                        durationMinutes = selectedMinute
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isScheduled
            ) {
                Text("Schedule")
            }
        }
    }
}

@Composable
private fun TaskCard(todo: TodoModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TASK",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = todo.title, style = MaterialTheme.typography.titleMedium)
            if (todo.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            SuggestionChip(
                onClick = {},
                label = { Text("${todo.priority} priority") }
            )
        }
    }
}

@Composable
private fun TimerCard(
    selectedHour: Int,
    selectedMinute: Int,
    selectedSecond: Int,
    remainingTime: String,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onSecondChange: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SET REMINDER TIME",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "This is not a clock. Enter how long from now you want to be notified.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "e.g. set 00:30:00 to get notified after 30 minutes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimePickerColumn(
                    label = "Hour",
                    value = selectedHour,
                    range = 0..23,
                    onValueChange = onHourChange
                )
                Text(":", style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 4.dp))
                TimePickerColumn(
                    label = "Min",
                    value = selectedMinute,
                    range = 0..59,
                    onValueChange = onMinuteChange
                )
                Text(":", style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 4.dp))
                TimePickerColumn(
                    label = "Sec",
                    value = selectedSecond,
                    range = 0..59,
                    onValueChange = onSecondChange
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Countdown", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(remainingTime, style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun TimePickerColumn(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
        }
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 52.dp)
                .border(
                    0.5.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString().padStart(2, '0'),
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Monospace
            )
        }
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ScheduledStatusBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Worker scheduled — notification will fire on time",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun calculateScheduledTime(hour: Int, minute: Int, second: Int): Long {
    val now = Clock.System.now().toEpochMilliseconds()
    val offsetMillis = ((hour * 3600) + (minute * 60) + second) * 1000L
    return now + offsetMillis
}