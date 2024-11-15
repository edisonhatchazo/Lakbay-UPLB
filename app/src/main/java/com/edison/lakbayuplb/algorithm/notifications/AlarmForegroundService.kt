package com.edison.lakbayuplb.algorithm.notifications
import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.data.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AlarmForegroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the foreground service with a single notification
        val channelId = getString(R.string.class_channel_id)
        val channelName = getString(R.string.class_channel_name)
        val channelDescription = getString(R.string.class_channel_description)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(channelName)
            .setContentText(channelName)
            .setSmallIcon(R.drawable.lakbay_uplb)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (checkLocationPermissions()) {
            startForeground(1, notification)

            // Logic to schedule or reschedule alarms
            serviceScope.launch {
                val appContainer = AppDataContainer(applicationContext)
                val classScheduleRepository = appContainer.classScheduleRepository
                val buildingRepository = appContainer.buildingRepository
                val examScheduleRepository = appContainer.examScheduleRepository

                val classSchedules = classScheduleRepository.getAllClassSchedules().firstOrNull() ?: emptyList()
                val examSchedules = examScheduleRepository.getAllExamsSchedules().firstOrNull() ?: emptyList()

                scheduleClassAlarms(applicationContext, classSchedules, buildingRepository, 60)
                scheduleExamAlarms(applicationContext, examSchedules, buildingRepository, 60)
                scheduleWeeklyExamSummary(applicationContext)
            }
        } else {
            stopSelf() // Stop the service if permissions are not available
        }

        return START_STICKY // Ensures service restarts if the system kills it
    }

    override fun onBind(intent: Intent?): IBinder? = null // Not binding to any activity

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Cancel any ongoing coroutines
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
