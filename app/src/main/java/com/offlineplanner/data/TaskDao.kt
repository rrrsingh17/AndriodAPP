package com.offlineplanner.data
import androidx.room.*; import kotlinx.coroutines.flow.Flow; import java.time.LocalDate
@Dao interface TaskDao {
@Query("SELECT * FROM tasks WHERE date = :date ORDER BY startTime IS NULL, startTime") fun tasksForDate(date: LocalDate): Flow<List<Task>>
@Query("SELECT * FROM tasks WHERE date BETWEEN :start AND :end ORDER BY date, startTime") fun tasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>>
@Query("SELECT * FROM tasks ORDER BY date DESC") fun allTasks(): Flow<List<Task>>
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(task: Task): Long
@Delete suspend fun delete(task: Task) }