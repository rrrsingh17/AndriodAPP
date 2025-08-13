package com.offlineplanner.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsView() {
    Column(Modifier.padding(16.dp)) {
        Text("Offline-only. No internet permission.")
        Text("Notifications require user consent on Android 13+.")
        Text("Custom tones via system ringtone picker.")
    }
}