package com.example.taskmanagement
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
//import org.intellij.lang.annotations.Flow
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskItemDao {
    @Query("SELECT * FROM task_item_table ORDER BY id ASC")
    fun allTaskItems(): Flow<List<TaskItem>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insertTaskItem(taskItem: TaskItem)
    @Update
    suspend fun updateTaskItem(taskItem: TaskItem)
    @Delete
    suspend fun deleteTaskItem(taskItem: TaskItem)
    @Query("DELETE FROM task_item_table WHERE id = :taskId")
    suspend fun deleteById(taskId: Int)
}

