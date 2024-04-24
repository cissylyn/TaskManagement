package com.example.taskmanagement

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.util.*
import androidx.lifecycle.asLiveData


class TaskViewModel(private  val repository: TaskItemRepository,  private val context: Context):ViewModel()
{

    private val notificationHelper = NotificationHelper(context)

    var taskItems: LiveData<List<TaskItem>> = repository.allTaskItems.asLiveData()
    //added
    private val _selectedTaskDescription = MutableLiveData<String>()
    val selectedTaskDescription: LiveData<String>
        get() = _selectedTaskDescription


    fun addTaskItem(newTask:TaskItem)=viewModelScope.launch {
       repository.insertTaskItem(newTask)
   }


    fun updateTaskItem(taskItem: TaskItem)=viewModelScope.launch{
        repository.updateTaskItem(taskItem)
    }

    fun deleteTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.deleteTaskItem(taskItem)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCompleted(taskItem: TaskItem)=viewModelScope.launch{
       if (!taskItem.isCompleted())
           taskItem.completedDateString = TaskItem.dateFormatter.format(LocalDate.now())
        repository.updateTaskItem(taskItem)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleNotification(taskItem: TaskItem) {
        taskItem.dueTime()?.let { dueTime ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, dueTime.hour)
                set(Calendar.MINUTE, dueTime.minute)
                set(Calendar.SECOND, 0)
            }

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("title", taskItem.name)
                putExtra("content", taskItem.desc)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskItem.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelNotification(taskId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun addTaskItem(newTask: TaskItem,description: String)= viewModelScope.launch {
        repository.insertTaskItem(newTask)
        _selectedTaskDescription.value = description

    }


}
class TaskItemModelFactory(private val repository: TaskItemRepository, val context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(repository, context)as T
        throw IllegalArgumentException("unknown class for view model")
    }
}