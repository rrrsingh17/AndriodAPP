package com.offlineplanner.ui

import android.app.Activity
import android.content.Intent
import android.provider.AlarmClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.offlineplanner.data.*
import com.offlineplanner.util.AlarmScheduler
import kotlinx.coroutines.launch
import java.time.*

@Composable
fun TaskRow(task: Task, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(12.dp),
        Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (task.notes.isNotBlank()) {
                Text(task.notes, style = MaterialTheme.typography.bodySmall)
            }
            val times = listOfNotNull(
                task.startTime?.toString(),
                task.endTime?.let { "– $it" }
            ).joinToString(" ")
            val recur = if (task.recurrence != RecurrenceType.NONE) {
                " (${task.recurrence} x${task.interval})"
            } else ""
            Text(
                "${task.date}${if (times.isNotBlank()) "  $times" else ""}$recur",
                style = MaterialTheme.typography.bodySmall
            )
        }
        TextButton(onClick = onDelete) {
            Text("Delete")
        }
    }
}

@Composable
fun TaskListView(repo: TaskRepository) {
    val scope = rememberCoroutineScope()
    val tasks by repo.allTasks().collectAsState(initial = emptyList())
    
    LazyColumn {
        items(tasks, key = { it.id }) { t ->
            TaskRow(t, {}, { scope.launch { repo.delete(t) } })
            Divider()
        }
    }
}

@Composable
fun DayView(repo: TaskRepository) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    val tasks by repo.tasksForDate(date).collectAsState(initial = emptyList())
    
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Button(onClick = { date = date.minusDays(1) }) {
                Text("<")
            }
            Text(date.toString(), style = MaterialTheme.typography.titleLarge)
            Button(onClick = { date = date.plusDays(1) }) {
                Text(">")
            }
        }
        Spacer(Modifier.height(8.dp))
        if (tasks.isEmpty()) {
            Text("No tasks for today.")
        } else {
            LazyColumn {
                items(tasks) { t ->
                    TaskRow(t, {}, {})
                    Divider()
                }
            }
        }
    }
}

@Composable
fun WeekView(repo: TaskRepository) {
    val today = LocalDate.now()
    var start by remember { mutableStateOf(today.with(java.time.DayOfWeek.MONDAY)) }
    val end = start.plusDays(6)
    val tasks by repo.tasksBetween(start, end).collectAsState(initial = emptyList())
    val grouped = tasks.groupBy { it.date }
    
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Button(onClick = { start = start.minusWeeks(1) }) {
                Text("<")
            }
            Text("${start} to ${end}", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { start = start.plusWeeks(1) }) {
                Text(">")
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            for (i in 0..6) {
                val day = start.plusDays(i.toLong())
                item {
                    Text(day.dayOfWeek.toString() + " " + day, fontWeight = FontWeight.Bold)
                }
                items(grouped[day] ?: emptyList()) { t ->
                    TaskRow(t, {}, {})
                }
                item {
                    Divider()
                }
            }
        }
    }
}

@Composable
fun MonthView(repo: TaskRepository) {
    var monthStart by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val firstDayOfGrid = monthStart.with(java.time.DayOfWeek.MONDAY)
        .minusDays(((monthStart.dayOfWeek.value + 6) % 7).toLong())
    val gridDates = (0 until 42).map { firstDayOfGrid.plusDays(it.toLong()) }
    val monthEnd = monthStart.plusMonths(1).minusDays(1)
    val tasks by repo.tasksBetween(monthStart, monthEnd).collectAsState(initial = emptyList())
    val counts = tasks.groupBy { it.date }.mapValues { it.value.size }
    
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Button(onClick = { monthStart = monthStart.minusMonths(1) }) {
                Text("<")
            }
            Text(
                monthStart.month.toString() + " " + monthStart.year,
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { monthStart = monthStart.plusMonths(1) }) {
                Text(">")
            }
        }
        Spacer(Modifier.height(8.dp))
        Column {
            for (week in 0 until 6) {
                Row(Modifier.fillMaxWidth()) {
                    for (dow in 0 until 7) {
                        val d = gridDates[week * 7 + dow]
                        val faded = d.month != monthStart.month
                        Card(Modifier.weight(1f).padding(2.dp)) {
                            Column(Modifier.padding(6.dp)) {
                                Text(
                                    d.dayOfMonth.toString(),
                                    color = if (faded) {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                val c = counts[d] ?: 0
                                if (c > 0) {
                                    Text("$c task(s)", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}