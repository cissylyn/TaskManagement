package com.example.taskmanagement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val taskName = intent.getStringExtra("taskName") ?: return

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.taskmanagement.NOTIFICATION_ACTION") {
            // Handle notification action here, for example, show a toast
            val message = intent.getStringExtra("message") ?: "Default Message"
            Toast.makeText(context, "Notification Action: $message", Toast.LENGTH_SHORT).show()

        val channelId = "task_reminder_channel"
        val builder = context?.let {
            NotificationCompat.Builder(it, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Task Reminder")
//                .setContentText("Don't forget to complete task: $taskName")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

//        with(NotificationManagerCompat.from(context)) {
//            notify(System.currentTimeMillis().toInt(), builder.build())
//        }
            // You can launch an activity here, or perform other actions based on the notification tap
        }
    }
}
