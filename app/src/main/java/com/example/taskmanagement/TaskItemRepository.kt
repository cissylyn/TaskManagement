package com.example.taskmanagement

import androidx.annotation.WorkerThread
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

class TaskItemRepository(private val taskItemDao: TaskItemDao) {
    val allTaskItems: Flow<List<TaskItem>> = taskItemDao.allTaskItems() // Corrected return type to Flow<List<TaskItem>>

    @WorkerThread
    suspend fun insertTaskItem(taskItem: TaskItem) {
        taskItemDao.insertTaskItem(taskItem)
    }

    @WorkerThread // Added @WorkerThread annotation
    suspend fun updateTaskItem(taskItem: TaskItem) {
        taskItemDao.updateTaskItem(taskItem)
    }

    @WorkerThread
    suspend fun deleteTaskItem(taskItem: TaskItem) {
        taskItemDao.deleteById(taskItem.id)
    }
}
