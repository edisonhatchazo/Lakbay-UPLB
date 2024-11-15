package com.edison.lakbayuplb.algorithm.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.edison.lakbayuplb.MainActivity
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.data.AppDataContainer
import com.edison.lakbayuplb.data.building.BuildingRepository
import com.edison.lakbayuplb.data.classes.ExamSchedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

fun examNotifications(context: Context, title: String, channelId: String, remainingTime: Long, examId: Int) {
    val message = "You’ll be late for your Exam! Start walking now!"
    val requestCode = 1002

    val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("destination", "ExamScheduleDetails")
        putExtra("examId", examId)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        requestCode,
        deepLinkIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val snoozeIntent = Intent(context, ExamNotificationActionReceiver::class.java).apply {
        action = "SNOOZE_ACTION"
        putExtra("remaining_time", remainingTime)
    }
    val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val stopIntent = Intent(context, ExamNotificationActionReceiver::class.java).apply {
        action = "STOP_ACTION"
        putExtra("remaining_time", remainingTime)
    }
    val stopPendingIntent = PendingIntent.getBroadcast(context, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.lakbay_uplb)
        .setContentTitle(title)
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .addAction(R.drawable.snooze_icon, "Snooze", snoozePendingIntent)
        .addAction(R.drawable.stop_icon, "Stop", stopPendingIntent)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(requestCode, builder.build())
    }
}

suspend fun scheduleExamAlarms(
    context: Context,
    examSchedules: List<ExamSchedule>,
    buildingRepository: BuildingRepository,
    delay: Long
) {
    val currentTime = LocalDateTime.now()

    examSchedules.forEach { examSchedule ->
        val destination = getRoomCoordinates(examSchedule.roomId, buildingRepository)

        var nextExamDateTime = getNextClassDateTime(examSchedule.day, examSchedule.time)
            ?: return@forEach

        var alarmTime = nextExamDateTime.minusMinutes(delay)
        while (alarmTime.isBefore(currentTime)) {
            nextExamDateTime = nextExamDateTime.plusWeeks(1)
            alarmTime = nextExamDateTime.minusMinutes(delay)
        }

        scheduleExamAlarm(context, alarmTime, examSchedule, destination, delay)
    }
}


@SuppressLint("ScheduleExactAlarm")
fun scheduleExamAlarm(
    context: Context,
    alarmTime: LocalDateTime,
    examSchedule: ExamSchedule,
    destination: GeoPoint,
    delay: Long
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val channelId = context.getString(R.string.exam_channel_id)
    val location = "${destination.longitude},${destination.latitude}"
    val intent = Intent(context, ExamsNotificationReceiver::class.java).apply {
        action = ACTION_SHOW_NOTIFICATION
        putExtra("notification_title",examSchedule.title)
        putExtra("exam_id",examSchedule.id)
        putExtra("notification_channel_id", channelId)
        putExtra("destination",location)
        putExtra("remaining_time",delay)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        examSchedule.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmInMillis = alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmInMillis, pendingIntent)
}


fun rescheduleExamAlarms(context: Context, delay: Long) {
    val examScheduleRepository = AppDataContainer(context).examScheduleRepository
    val buildingRepository = AppDataContainer(context).buildingRepository

    CoroutineScope(Dispatchers.Default).launch {
        val examSchedules = examScheduleRepository.getAllExamsSchedules().first()
        scheduleExamAlarms(context, examSchedules, buildingRepository, delay)
    }
}
suspend fun cancelAllExamAlarms(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val examScheduleRepository = AppDataContainer(context).examScheduleRepository
    val examSchedules = examScheduleRepository.getAllExamsSchedules().firstOrNull()

    if (!examSchedules.isNullOrEmpty()) {
        examSchedules.forEach { examSchedule ->
            val intent = Intent(context, ExamsNotificationReceiver::class.java).apply {
                action = ACTION_SHOW_NOTIFICATION
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                examSchedule.id, // Unique request code for each exam
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    // Cancel weekly summary alarm
    val weeklyIntent = Intent(context, WeeklyExamSummaryReceiver::class.java).apply {
        action = "WEEKLY_EXAM_SUMMARY"
    }

    val weeklyPendingIntent = PendingIntent.getBroadcast(
        context,
        2001, // Unique request code for weekly exam summary
        weeklyIntent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )

    if (weeklyPendingIntent != null) {
        alarmManager.cancel(weeklyPendingIntent)
        weeklyPendingIntent.cancel()
    }
}


fun scheduleWeeklyExamSummary(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WeeklyExamSummaryReceiver::class.java).apply {
        action = "WEEKLY_EXAM_SUMMARY"
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        2001,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY * 7, // Repeat every 7 days
        pendingIntent
    )
}

fun sendWeeklyNotification(context: Context, message: String) {
    val channelId = context.getString(R.string.exam_channel_id)
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.lakbay_uplb)
        .setContentTitle("Exam Reminder")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(2001, builder.build())
    }
}



