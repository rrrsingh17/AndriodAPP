package com.offlineplanner.ui

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.AlarmClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.offlineplanner.data.*
import com.offlineplanner.util.AlarmScheduler
import kotlinx.coroutines.launch
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskView(repo: TaskRepository, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var start by remember { mutableStateOf<LocalTime?>(LocalTime.now().withSecond(0).withNano(0)) }
    var end by remember { mutableStateOf<LocalTime?>(null) }
    var hasAlarm by remember { mutableStateOf(true) }
    var alarmKind by remember { mutableStateOf(AlarmKind.SYSTEM_CLOCK) }
    var recurrence by remember { mutableStateOf(RecurrenceType.NONE) }
    var interval by remember { mutableStateOf(1) }
    var weekdays by remember { mutableStateOf(setOf<Int>()) }
    var untilDate by remember { mutableStateOf<LocalDate?>(null) }
    var ringtoneUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        if (r.resultCode == Activity.RESULT_OK) {
            ringtoneUri = r.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        }
    }

    fun openPicker() {
        val i = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri)
        }
        picker.launch(i)
    }

    Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.spacedBy(12.dp)) {
        Text("New Task", style = MaterialTheme.typography.titleLarge)
        
        OutlinedTextField(
            title, { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            notes, { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                date.toString(),
                { runCatching { LocalDate.parse(it) }.onSuccess { d -> date = d } },
                label = { Text("Date (YYYY-MM-DD)") }
            )
            OutlinedTextField(
                start?.toString() ?: "",
                { start = it.takeIf { it.isNotBlank() }?.let { s -> runCatching { LocalTime.parse(s) }.getOrNull() } },
                label = { Text("Start (HH:MM)") }
            )
            OutlinedTextField(
                end?.toString() ?: "",
                { end = it.takeIf { it.isNotBlank() }?.let { s -> runCatching { LocalTime.parse(s) }.getOrNull() } },
                label = { Text("End (HH:MM)") }
            )
        }
        
        Row {
            Checkbox(hasAlarm, { hasAlarm = it })
            Text("Reminder/Alarm")
        }
        
        if (hasAlarm) {
            Row {
                RadioButton(alarmKind == AlarmKind.SYSTEM_CLOCK, { alarmKind = AlarmKind.SYSTEM_CLOCK })
                Text("System Clock")
                Spacer(Modifier.width(16.dp))
                RadioButton(alarmKind == AlarmKind.IN_APP, { alarmKind = AlarmKind.IN_APP })
                Text("In-app notification")
            }
            Button(onClick = { openPicker() }) {
                Text(if (ringtoneUri == null) "Pick custom alarm tone" else "Change tone")
            }
        }
        
        Row {
            Text("Repeat: ")
            Spacer(Modifier.width(8.dp))
            DropdownMenuBox(recurrence) { recurrence = it }
            Spacer(Modifier.width(8.dp))
            if (recurrence != RecurrenceType.NONE) {
                OutlinedTextField(
                    interval.toString(),
                    { v -> v.toIntOrNull()?.let { interval = it.coerceAtLeast(1) } },
                    label = { Text("Interval") },
                    modifier = Modifier.width(120.dp)
                )
            }
        }
        
        if (recurrence == RecurrenceType.WEEKLY) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Sun" to 1, "Mon" to 2, "Tue" to 3, "Wed" to 4, "Thu" to 5, "Fri" to 6, "Sat" to 7).forEach { (l, v) ->
                    FilterChip(
                        checked = weekdays.contains(v),
                        onCheckedChange = { weekdays = if (it) weekdays + v else weekdays - v },
                        label = { Text(l) }
                    )
                }
            }
        }
        
        if (recurrence != RecurrenceType.NONE) {
            OutlinedTextField(
                untilDate?.toString() ?: "",
                { untilDate = it.takeIf { it.isNotBlank() }?.let { s -> runCatching { LocalDate.parse(s) }.getOrNull() } },
                label = { Text("Until (YYYY-MM-DD) – optional") }
            )
        }
        
        Button(onClick = {
            val task = Task(
                title = title, notes = notes, date = date, startTime = start, endTime = end,
                hasAlarm = hasAlarm, alarmKind = alarmKind, recurrence = recurrence,
                interval = interval, byWeekdays = weekdays.sorted().joinToString(","),
                untilDate = untilDate, ringtoneUri = ringtoneUri?.toString()
            )
            kotlinx.coroutines.GlobalScope.launch { repo.upsert(task) }
            
            if (hasAlarm) {
                if (alarmKind == AlarmKind.SYSTEM_CLOCK) {
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        if (task.startTime != null) {
                            putExtra(AlarmClock.EXTRA_HOUR, task.startTime!!.hour)
                            putExtra(AlarmClock.EXTRA_MINUTES, task.startTime!!.minute)
                        }
                        putExtra(AlarmClock.EXTRA_MESSAGE, task.title)
                        if (task.recurrence == RecurrenceType.WEEKLY && task.byWeekdays.isNotBlank()) {
                            val days = task.byWeekdays.split(",").mapNotNull { it.toIntOrNull() }
                            putIntegerArrayListExtra(AlarmClock.EXTRA_DAYS, java.util.ArrayList(days))
                        }
                        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                    }
                    androidx.compose.ui.platform.LocalContext.current.startActivity(intent)
                } else {
                    AlarmScheduler.schedule(androidx.compose.ui.platform.LocalContext.current, task)
                }
            }
            onDone()
        }) {
            Text("Save")
        }
    }
}

@Composable
fun DropdownMenuBox(value: RecurrenceType, onChange: (RecurrenceType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { expanded = true }) {
        Text(value.name)
    }
    DropdownMenu(expanded, { expanded = false }) {
        RecurrenceType.values().forEach { t ->
            DropdownMenuItem(
                { Text(t.name) },
                { onChange(t); expanded = false }
            )
        }
    }
}