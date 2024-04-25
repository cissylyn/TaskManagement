import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*
import android.os.Handler
import com.example.taskmanagement.R

class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "task_notification_channel"
    private val NOTIFICATION_ID = 123

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val descriptionText = "Channel for Task Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(taskDescription: String, dueTimeInMillis: Long) {
        val currentTimeInMillis = Calendar.getInstance().timeInMillis
        val delayInMillis = dueTimeInMillis - currentTimeInMillis

        Handler().postDelayed({
            showNotification(taskDescription)
        }, delayInMillis)
    }

    private fun showNotification(taskDescription: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentTitle("Task Reminder")
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
