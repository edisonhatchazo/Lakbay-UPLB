package com.edison.lakbayuplb.algorithm.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.edison.lakbayuplb.LakbayUPLBApplication
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.data.AppDataContainer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ClassesNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SHOW_NOTIFICATION) {
            val channelId = intent.getStringExtra("notification_channel_id") ?: context.getString(R.string.class_channel_id)
            val classId = intent.getIntExtra("class_id", 1)
            val title = intent.getStringExtra("notification_title") ?: "Course"
            val destination = intent.getStringExtra("destination") ?: "121.24150742562976,14.165008914904659"
            val remainingTime = intent.getLongExtra("remaining_time", 5)
            val appContainer = (context.applicationContext as LakbayUPLBApplication).container
            val routingRepository = appContainer.localRoutingRepository
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                location?.let {
                    val userCoordinates = "${location.longitude},${location.latitude}"
                    val route = routingRepository.getRoute(context, "foot", userCoordinates, destination)

                    if (route.isNotEmpty()) {
                        val totalDistance = route.first().route.distance
                        val walkingSpeed = 0.1
                        val estimatedDuration = totalDistance / walkingSpeed

                        if (estimatedDuration > remainingTime) {
                            // Show the notification
                            classNotifications(context, title, channelId, remainingTime, classId)
                            scheduleInexactRepeatingAlarm(
                                context = context,
                                classId = classId,
                                title = title,
                                channelId = channelId,
                                destination = destination,
                                remainingTime = remainingTime-5)

                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("Classes", "Failed to get location", e)
            }
        }
    }

    private fun scheduleInexactRepeatingAlarm(
        context: Context,
        classId: Int,
        title: String,
        channelId: String,
        destination: String,
        remainingTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ClassesNotificationReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
            putExtra("notification_title",title)
            putExtra("class_id",classId)
            putExtra("notification_channel_id", channelId)
            putExtra("destination",destination)
            putExtra("remaining_time",remainingTime)
        }

        val repeatPendingIntent = PendingIntent.getBroadcast(
            context,
            classId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerTime = SystemClock.elapsedRealtime() + 5 * 60 * 1000 // 5 minutes from now
        val repeatInterval = 5 * 60 * 1000 // 5-minute interval

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            repeatInterval.toLong(),
            repeatPendingIntent
        )
    }
}


class ClassNotificationActionReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        val remainingTime = intent.getLongExtra("remaining_time", 60)
        var action = intent.action
        val classId = intent.getIntExtra("class_id", -1)

        when (action) {
            "SNOOZE_ACTION" -> {
                // Snooze the notification by 5 minutes
                val snoozeTime = System.currentTimeMillis() + 5 * 60 * 1000
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val snoozeIntent = Intent(context, ClassesNotificationReceiver::class.java).apply {
                    action = ACTION_SHOW_NOTIFICATION
                    putExtra("remaining_time", remainingTime - 5)
                    putExtra("class_id", classId)
                }

                val snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    classId,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, snoozePendingIntent)
            }
            "STOP_ACTION" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    cancelAllClassAlarms(context)
                    rescheduleClassAlarms(context, 60) // Reset delay to 60 for future alarms
                }
            }
        }

        // Dismiss the current notification
        NotificationManagerCompat.from(context).cancel(1001)
    }
}



class ExamsNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SHOW_NOTIFICATION) {
            val channelId = intent.getStringExtra("notification_channel_id") ?: context.getString(R.string.exam_channel_id)
            val examId = intent.getIntExtra("exam_id", 1)
            val title = intent.getStringExtra("notification_title") ?: "Course"
            val destination = intent.getStringExtra("destination") ?: "121.24150742562976,14.165008914904659"
            val remainingTime = intent.getLongExtra("remaining_time", 5)
            val appContainer = (context.applicationContext as LakbayUPLBApplication).container
            val routingRepository = appContainer.localRoutingRepository
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                location?.let {
                    val userCoordinates = "${location.longitude},${location.latitude}"
                    val route = routingRepository.getRoute(context, "foot", userCoordinates, destination)

                    if (route.isNotEmpty()) {
                        val totalDistance = route.first().route.distance
                        val walkingSpeed = 0.1
                        val estimatedDuration = totalDistance / walkingSpeed

                        if (estimatedDuration > remainingTime) {
                            // Show the notification
                            examNotifications(context, title, channelId, remainingTime, examId)
                            scheduleInexactRepeatingAlarm(
                                context = context,
                                examId = examId,
                                title = title,
                                channelId = channelId,
                                destination = destination,
                                remainingTime = remainingTime-5)

                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("Classes", "Failed to get location", e)
            }
        }
    }

    private fun scheduleInexactRepeatingAlarm(
        context: Context,
        examId: Int,
        title: String,
        channelId: String,
        destination: String,
        remainingTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ExamsNotificationReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
            putExtra("notification_title",title)
            putExtra("exam_id",examId)
            putExtra("notification_channel_id", channelId)
            putExtra("destination",destination)
            putExtra("remaining_time",remainingTime)
        }

        val repeatPendingIntent = PendingIntent.getBroadcast(
            context,
            examId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerTime = SystemClock.elapsedRealtime() + 5 * 60 * 1000 // 5 minutes from now
        val repeatInterval = 5 * 60 * 1000 // 5-minute interval

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            repeatInterval.toLong(),
            repeatPendingIntent
        )
    }
}


class ExamNotificationActionReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        val remainingTime = intent.getLongExtra("remaining_time", 60)
        var action = intent.action
        val examId = intent.getIntExtra("exam_id", -1)

        when (action) {
            "SNOOZE_ACTION" -> {
                // Snooze the notification by 5 minutes
                val snoozeTime = System.currentTimeMillis() + 5 * 60 * 1000
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val snoozeIntent = Intent(context, ExamsNotificationReceiver::class.java).apply {
                    action = ACTION_SHOW_NOTIFICATION
                    putExtra("remaining_time", remainingTime - 5)
                    putExtra("exam_id", examId)
                }

                val snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    examId,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, snoozePendingIntent)
            }
            "STOP_ACTION" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    cancelAllExamAlarms(context)
                    rescheduleExamAlarms(context, 60) // Reset delay to 60 for future alarms
                }
            }
        }
        // Dismiss the current notification
        NotificationManagerCompat.from(context).cancel(1002)
    }
}


class BootCompletedReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Check if all required permissions are granted
            val permissionsGranted = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            ).all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }

            if (permissionsGranted) {
                startAlarmService(context)
            }
        }
    }
}

class WeeklyExamSummaryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val examRepository = AppDataContainer(context).examScheduleRepository
            getWeeklyExamSummary(context, examRepository)
        }
    }
}
