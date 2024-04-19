package com.example.taskmanagement

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

import androidx.lifecycle.asLiveData


import java.time.LocalTime


class TaskViewModel(private  val repository: TaskItemRepository):ViewModel()
{
    var taskItems: LiveData<List<TaskItem>> = repository.allTaskItems.asLiveData()


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
}
class TaskItemModelFactory(private val repository: TaskItemRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(repository)as T
        throw IllegalArgumentException("unknown class for view model")
    }
}