package com.example.taskmanagement

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagement.databinding.ActivityMainBinding
import com.example.taskmanagement.databinding.TaskItemCellBinding
import java.io.File

class MainActivity : AppCompatActivity(),TaskItemClickListener {

    companion object {
        const val FILE_PICKER_REQUEST_CODE = 1001
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var itembinding: TaskItemCellBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((application as TodoApplication).repository, this)

    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        itembinding = TaskItemCellBinding.inflate(layoutInflater)
        setContentView(binding.root)


        createNotificationChannel()

        // Example task name
        val taskName = "dont forget to complete your tasks"

        // Build notification
        val notificationBuilder = buildNotification(this, taskName)

        // Generate unique notification ID
        val notificationId = System.currentTimeMillis().toInt()

        // Display notification
        showNotification(this, notificationId, notificationBuilder)



        binding.newTaskButton.setOnClickListener {
            NewTaskSheet(taskItem = null).show(supportFragmentManager, "newTaskTag")

        }

        setRecyclerView()

        itembinding.uploadButton.setOnClickListener {
            openFilePicker()
        }

        // Observe changes in the task list and update the progress bar
        taskViewModel.taskItems.observe(this) { tasks ->
            val progress = taskViewModel.calculateProgress(tasks)
            binding.progressBar.progress = progress
        }


        //setContentView(R.layout.activity_main),i commented
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCompletionHint() {
        val tasks = taskViewModel.taskItems.value // Get the current list of tasks
        tasks?.let {
            val completedTasks = tasks.count { it.isCompleted() }
            val totalTasks = it.size
            val completionText = "$completedTasks/$totalTasks"
            binding.taskCompletionHint.text = completionText
            binding.progressBar.progress = completedTasks
        }
    }



    // Assuming 'tasksCompleted' is the number of completed tasks and 'totalTasks' is the total number of tasks


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setRecyclerView() {
        val mainActivity = this
        taskViewModel.taskItems.observe(this) {
            binding.todoListRecycleView.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskItemAdapter(it, mainActivity)
                updateCompletionHint()

            }
        }
    }

    override fun editTaskitem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(supportFragmentManager, "newTaskTag")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setCompleted(taskItem)
        updateCompletionHint()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
        updateCompletionHint()
    }

    fun onUploadButtonClick(view: View) {
        openFilePicker()
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



    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"  // Allow any file type to be selected
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedFileUri: Uri? = data?.data
            if (selectedFileUri != null) {
                uploadFile(selectedFileUri)
            }
        }
    }

    private fun uploadFile(fileUri: Uri?) {
        if (fileUri == null) {
            Toast.makeText(this, "File URI is null", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = getFileName(fileUri)
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Failed to get file name", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Selected file: $fileName", Toast.LENGTH_SHORT).show()

        try {
            val inputStream = contentResolver.openInputStream(fileUri)
            val outputStream = openFileOutput(fileName, Context.MODE_PRIVATE)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()

            // Update the TextView in the CardView to display the selected file name
            itembinding.fileInfo.text = "Selected File: $fileName"
            itembinding.fileInfo.visibility = View.VISIBLE

        } catch (e: Exception) {
            Toast.makeText(this, "Error saving file: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }



    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var fileName: String? = null

        when {
            uri.scheme == "content" -> {
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        fileName = it.getString(it.getColumnIndexOrThrow("_display_name"))
                    }
                }
            }

            uri.scheme == "file" -> {
                fileName = File(uri.path).name
            }
        }

        return fileName ?: "Unknown"
    }

}