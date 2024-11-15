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
import com.edison.lakbayuplb.data.classes.ClassSchedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.ZoneId


fun classNotifications(context: Context, title: String, channelId: String, remainingTime: Long, classId: Int) {
    val message = "You’ll be late! Start walking now!"
    val requestCode = 1001
    // Set up the deep link for the notification
    val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("destination", "ClassScheduleDetails")
        putExtra("classId", classId)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        requestCode,
        deepLinkIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val snoozeIntent = Intent(context, ClassNotificationActionReceiver::class.java).apply {
        action = "SNOOZE_ACTION"
        putExtra("remaining_time", remainingTime)
    }
    val snoozePendingIntent = PendingIntent.getBroadcast(context, requestCode, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    // Intent for stop action
    val stopIntent = Intent(context, ClassNotificationActionReceiver::class.java).apply {
        action = "STOP_ACTION"
        putExtra("remaining_time", remainingTime)
    }
    val stopPendingIntent = PendingIntent.getBroadcast(context, requestCode, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

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
            // Handle permission check
            return
        }
        notify(requestCode, builder.build())
    }
}


suspend fun scheduleClassAlarms(
    context: Context,
    classSchedules: List<ClassSchedule>,
    buildingRepository: BuildingRepository,
    delay: Long
) {
    val currentTime = LocalDateTime.now()

    classSchedules.forEach { classSchedule ->
        val destination = getRoomCoordinates(classSchedule.roomId, buildingRepository)

        var nextClassDateTime = getNextClassDateTime(classSchedule.days, classSchedule.time)
            ?: return@forEach

        var alarmTime = nextClassDateTime.minusMinutes(delay)
        while (alarmTime.isBefore(currentTime)) {
            // Move to the next scheduled class time
            nextClassDateTime = nextClassDateTime.plusWeeks(1)
            alarmTime = nextClassDateTime.minusMinutes(delay)
        }
        scheduleClassAlarm(context, alarmTime, classSchedule, destination, delay)
    }
}


@SuppressLint("ScheduleExactAlarm")
fun scheduleClassAlarm(
    context: Context,
    alarmTime: LocalDateTime,
    classSchedule: ClassSchedule,
    destination: GeoPoint,
    delay: Long
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val channelId = context.getString(R.string.class_channel_id)
    val location = "${destination.longitude},${destination.latitude}"
    val intent = Intent(context, ClassesNotificationReceiver::class.java).apply {
        action = ACTION_SHOW_NOTIFICATION
        putExtra("notification_title",classSchedule.title)
        putExtra("class_id",classSchedule.id)
        putExtra("notification_channel_id", channelId)
        putExtra("destination",location)
        putExtra("remaining_time",delay)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        classSchedule.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmInMillis = alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmInMillis, pendingIntent)
}


fun rescheduleClassAlarms(context: Context, delay: Long) {
    val classScheduleRepository = AppDataContainer(context).classScheduleRepository
    val buildingRepository = AppDataContainer(context).buildingRepository

    CoroutineScope(Dispatchers.Default).launch {
        val classSchedules = classScheduleRepository.getAllClassSchedules().first()
        scheduleClassAlarms(context, classSchedules, buildingRepository, delay)
    }
}

suspend fun cancelAllClassAlarms(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Fetch all class schedules from the repository
    val classScheduleRepository = AppDataContainer(context).classScheduleRepository
    val classSchedules = classScheduleRepository.getAllClassSchedules().firstOrNull()

    if (classSchedules.isNullOrEmpty())
        return


    // Iterate over all class schedules and cancel their alarms
    classSchedules.forEach { classSchedule ->
        val intent = Intent(context, ClassesNotificationReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            classSchedule.id, // Use the unique classId
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
