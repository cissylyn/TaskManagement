package com.example.taskmanagement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("taskName") ?: return

        val channelId = "task_reminder_channel"
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Task Reminder")
            .setContentText("Don't forget to complete task: $taskName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

//        with(NotificationManagerCompat.from(context)) {
//            notify(System.currentTimeMillis().toInt(), builder.build())
//        }
    }
}
