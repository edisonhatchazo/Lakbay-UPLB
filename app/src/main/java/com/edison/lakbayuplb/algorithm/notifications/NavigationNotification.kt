package com.edison.lakbayuplb.algorithm.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.edison.lakbayuplb.R

class NavigationNotificationService : Service() {

    private val notificationId = 1003 // Unique ID for the navigation notification
    private lateinit var title: String
    private var currentInstruction = "No instructions available."
    private var handler: Handler? = null
    private val updateInterval = 2000L // 2 seconds
    private lateinit var runnable: Runnable
    private var receiver: BroadcastReceiver? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "STOP_SERVICE" -> {
                stopSelf()
                stopNotificationUpdates()
                return START_NOT_STICKY
            }
            else -> {
                title = intent?.getStringExtra("title") ?: "Navigation"
                currentInstruction = intent?.getStringExtra("currentInstruction") ?: currentInstruction
                registerReceiverSafely()
                startForeground(notificationId, createNotification())
                startNotificationUpdates()
            }
        }
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val notificationChannelId = getString(R.string.navigation_channel_id)

        return NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.lakbay_uplb)
            .setContentTitle(title)
            .setContentText(currentInstruction)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true) // Ensures the notification is not dismissible
            .build()
    }

    private fun startNotificationUpdates() {
        handler = Handler(mainLooper)
        runnable = Runnable {
            updateNotification()
            handler?.postDelayed(runnable, updateInterval)
        }
        handler?.post(runnable)
    }

    private fun stopNotificationUpdates() {
        handler?.removeCallbacks(runnable)
        handler = null
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification()
        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun registerReceiverSafely() {
        if (receiver == null) {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == "UPDATE_NAVIGATION_INSTRUCTION") {
                        currentInstruction = intent.getStringExtra("currentInstruction") ?: currentInstruction
                        updateNotification()
                    }
                }
            }
            val filter = IntentFilter("UPDATE_NAVIGATION_INSTRUCTION")
            registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        stopNotificationUpdates()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        stopNotificationUpdates()
        receiver?.let {
            unregisterReceiver(it)
            receiver = null
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

