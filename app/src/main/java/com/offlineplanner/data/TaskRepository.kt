package com.offlineplanner.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepository(private val dao: TaskDao) {
    fun tasksForDate(date: LocalDate): Flow<List<Task>> = dao.tasksForDate(date)
    fun tasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>> = dao.tasksBetween(start, end)
    fun allTasks(): Flow<List<Task>> = dao.allTasks()
    suspend fun upsert(task: Task) = dao.upsert(task)
    suspend fun delete(task: Task) = dao.delete(task)
}