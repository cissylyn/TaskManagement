package com.example.taskmanagement

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),TaskItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((application as TodoApplication).repository, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //added
        val filter = IntentFilter("com.example.taskmanagement.NOTIFICATION_ACTION")
        registerReceiver(notificationReceiver, filter)
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

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(notificationReceiver)
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Handle notification action here
            val message = intent?.getStringExtra("message")
            // For example, show a toast
            message?.let {
                Toast.makeText(context, "Notification Action: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }


}