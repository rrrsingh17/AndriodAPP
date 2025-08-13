package com.offlineplanner.util

import android.app.*
import android.content.*
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.offlineplanner.MainActivity
import com.offlineplanner.R
import com.offlineplanner.data.*
import java.time.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Task Reminder"
        val ringtone = intent.getStringExtra("ringtone") ?: ""
        val recurrence = RecurrenceType.valueOf(intent.getStringExtra("recurrence") ?: "NONE")
        val interval = intent.getIntExtra("interval", 1)
        val byWeekdays = intent.getStringExtra("byWeekdays") ?: ""
        val until = intent.getStringExtra("until")?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        
        val channelId = "task_ch_" + ringtone.hashCode()
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (nm.getNotificationChannel(channelId) == null) {
            val ch = NotificationChannel(channelId, "Task reminders", NotificationManager.IMPORTANCE_HIGH)
            if (ringtone.isNotBlank()) {
                val soundUri = Uri.parse(ringtone)
                val attrs = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                ch.setSound(soundUri, attrs)
            }
            nm.createNotificationChannel(ch)
        }
        
        val content = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Reminder")
            .setContentText(title)
            .setAutoCancel(true)
            .setContentIntent(content)
            .build()
        
        nm.notify(System.currentTimeMillis().toInt(), notif)
        
        val task = Task(
            id = 0, title = title, notes = "", date = LocalDate.now(),
            startTime = LocalTime.now(), hasAlarm = true, alarmKind = AlarmKind.IN_APP,
            recurrence = recurrence, interval = interval, byWeekdays = byWeekdays,
            untilDate = until, ringtoneUri = if (ringtone.isBlank()) null else ringtone
        )
        
        val next = AlarmScheduler.nextOccurrenceDateTime(task, LocalDateTime.now())
        if (until == null || next.toLocalDate().isBefore(until.plusDays(1))) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pi = AlarmScheduler.pendingIntent(context, task)
            am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                next.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                pi
            )
        }
    }
}