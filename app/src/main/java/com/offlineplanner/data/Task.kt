package com.offlineplanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY
}

enum class AlarmKind {
    SYSTEM_CLOCK, IN_APP
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String = "",
    val date: LocalDate,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val hasAlarm: Boolean = false,
    val alarmKind: AlarmKind = AlarmKind.SYSTEM_CLOCK,
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val interval: Int = 1,
    val byWeekdays: String = "",
    val untilDate: LocalDate? = null,
    val ringtoneUri: String? = null
)