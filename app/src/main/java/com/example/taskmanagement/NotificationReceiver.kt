package com.example.taskmanagement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title")
        val content = intent?.getStringExtra("content")

        if (context != null && title != null && content != null) {
            val notificationHelper = NotificationHelper(context)
            val notification = notificationHelper.createNotification(
                title,
                content,
                Intent(context, MainActivity::class.java)
            )
            notificationHelper.notificationManager.notify(1, notification)
        }
    }
}
