package com.offlineplanner.ui
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.offlineplanner.data.TaskRepository
@Composable fun AppScaffold(nav: NavHostController, repo: TaskRepository) {
val items = listOf("Day","Week","Month","Tasks","Settings")
Scaffold(bottomBar = {
NavigationBar { val backStackEntry by nav.currentBackStackEntryAsState(); val current = backStackEntry?.destination?.route
items.forEach { label -> NavigationBarItem(selected = current == label, onClick = { nav.navigate(label) { launchSingleTop = true } }, label = { Text(label) }, icon = { Icon(Icons.Filled.Add, null) }) } } },
floatingActionButton = { FloatingActionButton(onClick = { nav.navigate("EditTask") }) { Icon(Icons.Filled.Add, null) } }
) { inner -> NavHost(nav, "Month", Modifier.padding(inner)) {
composable("Day") { DayView(repo) }; composable("Week") { WeekView(repo) }; composable("Month") { MonthView(repo) }
composable("Tasks") { TaskListView(repo) }; composable("Settings") { SettingsView() }
composable("EditTask") { EditTaskView(repo, onDone = { nav.popBackStack() }) } } } }