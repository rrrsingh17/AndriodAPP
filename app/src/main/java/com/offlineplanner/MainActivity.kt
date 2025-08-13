package com.offlineplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.offlineplanner.data.AppDatabase
import com.offlineplanner.data.TaskRepository
import com.offlineplanner.ui.AppScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = AppDatabase.getInstance(applicationContext)
        val repo = TaskRepository(db.taskDao())
        
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                AppScaffold(nav, repo)
            }
        }
    }
}