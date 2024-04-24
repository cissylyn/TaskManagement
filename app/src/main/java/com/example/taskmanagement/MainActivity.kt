package com.example.taskmanagement

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),TaskItemClickListener {

    private lateinit var welcomeTextView: TextView


    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((application as TodoApplication).repository, this)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        // Example task name
        val taskName = "Example Task"

        // Build notification
        val notificationBuilder = buildNotification(this, taskName)

        // Generate unique notification ID
        val notificationId = System.currentTimeMillis().toInt()

        // Display notification
        showNotification(this, notificationId, notificationBuilder)


        //added
////        val filter = IntentFilter("com.example.taskmanagement.NOTIFICATION_ACTION")
//        val filter = IntentFilter("com.example.taskmanagement.NOTIFICATION_ACTION")
//        registerReceiver(notificationReceiver, filter, null, null, Context.BIND_AUTO_CREATE or Context.RECEIVER_VISIBLE_TO_INSTANT_APPS)

//added
        binding.newTaskButton.setOnClickListener {
            NewTaskSheet(taskItem = null).show(supportFragmentManager, "newTaskTag")

        }

        setRecyclerView()

//        testNotification()


        //setContentView(R.layout.activity_main),i commented
    }

    private fun setRecyclerView() {
        val mainActivity = this
        taskViewModel.taskItems.observe(this) {
            binding.todoListRecycleView.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskItemAdapter(it, mainActivity)

            }
        }
    }

    override fun editTaskitem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(supportFragmentManager, "newTaskTag")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addOrUpdateNotification(taskItem: TaskItem) {
        // Cancel existing notification if any
        taskViewModel.cancelNotification(taskItem.id)

        // Schedule new notification if task has a due time
        if (taskItem.dueTime() != null) {
            taskViewModel.scheduleNotification(taskItem)
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "task_reminder_channel"
            val channelName = "Task Reminder Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for task reminders"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNotification(taskItem: TaskItem) {
        val notificationId = taskItem.id // Unique id for each notification
        val channelId = "task_reminder_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.notification_icon) // Set your notification icon
            setContentTitle("Task Reminder")
            setContentText("Don't forget to complete task: ${taskItem.name}")
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // You can add more customization here, such as setting the notification sound or action
        }

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }



    // Function to show notification
    private fun showNotification(
        notificationId1: MainActivity,
        notificationId: Int,
        notificationBuilder: NotificationCompat.Builder
    ) {
        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }





    fun buildNotification(context: Context, taskName: String): NotificationCompat.Builder {
        val channelId = "task_reminder_channel"
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Task Reminder")
            .setContentText("Don't forget to complete task: $taskName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

//    private fun testNotification() {
//        val title = "Test Notification"
//        val content = "Thiiiissssss is a test"
//        try {
//            val intent = Intent(this, NotificationReceiver::class.java)
//            intent.putExtra("title", title)
//            intent.putExtra("content", content)
//
//            val notification = NotificationHelper(this).createNotification(title, content, intent)
//
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.notify(1, notification)
//        } catch (e:Exception)
//        {
//            Log.e("NotificationTest", "Error creating notification", e)
//        }
//    }


}