package com.offlineplanner.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import com.offlineplanner.data.*
import java.time.*
import java.util.*

object AlarmScheduler {
    fun schedule(context: Context, task: Task) {
        val start = task.startTime ?: return
        val firstTime = nextOccurrenceDateTime(task, LocalDateTime.now())
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context, task)
        am.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            firstTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            pi
        )
    }

    internal fun pendingIntent(ctx: Context, task: Task): PendingIntent {
        val i = Intent(ctx, AlarmReceiver::class.java).apply {
            putExtra("title", task.title)
            putExtra("ringtone", task.ringtoneUri ?: "")
            putExtra("recurrence", task.recurrence.name)
            putExtra("interval", task.interval)
            putExtra("byWeekdays", task.byWeekdays)
            putExtra("until", task.untilDate?.toString() ?: "")
        }
        return PendingIntent.getBroadcast(
            ctx, task.title.hashCode(), i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun nextOccurrenceDateTime(task: Task, from: LocalDateTime): LocalDateTime {
        val time = task.startTime ?: LocalTime.of(9, 0)
        var candidate = LocalDateTime.of(task.date, time)
        
        if (candidate.isAfter(from)) return candidate
        
        return when (task.recurrence) {
            RecurrenceType.NONE -> from.plusMinutes(1)
            RecurrenceType.DAILY -> {
                val days = task.interval.coerceAtLeast(1)
                var c = candidate
                while (!c.isAfter(from)) c = c.plusDays(days.toLong())
                c
            }
            RecurrenceType.WEEKLY -> {
                val selected = task.byWeekdays.split(",").mapNotNull { it.toIntOrNull() }.toSet()
                var cursor = from.toLocalDate()
                while (true) {
                    val w = (cursor.dayOfWeek.value % 7) + 1
                    if (selected.isEmpty() || selected.contains(w)) {
                        val dt = LocalDateTime.of(cursor, time)
                        if (dt.isAfter(from)) return dt
                    }
                    cursor = cursor.plusDays(1)
                }
            }
            RecurrenceType.MONTHLY -> {
                val months = task.interval.coerceAtLeast(1)
                var c = candidate
                while (!c.isAfter(from)) c = c.plusMonths(months.toLong())
                c
            }
        }
    }
}