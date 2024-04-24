package com.example.taskmanagement
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.taskmanagement.NOTIFICATION_ACTION") {
            // Handle notification action here, for example, show a toast
            val message = intent.getStringExtra("message") ?: "Default Message"
            Toast.makeText(context, "Notification Action: $message", Toast.LENGTH_SHORT).show()

            // You can launch an activity here, or perform other actions based on the notification tap
        }
    }
}
