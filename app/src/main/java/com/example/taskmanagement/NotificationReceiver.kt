import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.taskmanagement.R
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            // Get task details from intent
            val taskDescription = intent?.getStringExtra("task_description")
            val taskTimeInMillis = intent?.getLongExtra("task_time", 0L)

            // Check if it's time to show the notification
            val currentTime = Calendar.getInstance().timeInMillis
            if (currentTime >= taskTimeInMillis!!) {
                // Build the notification
                val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                    .setContentTitle("Task Reminder")
                    .setContentText(taskDescription)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Display the notification
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0, notificationBuilder.build())
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "task_notification_channel"
    }
}
