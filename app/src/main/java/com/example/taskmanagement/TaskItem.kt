package com.example.taskmanagement

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "task_item_table")
class TaskItem (
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="desc") var desc: String,
    @ColumnInfo(name="dueTimeString") var dueTimeString: String?,
    @ColumnInfo(name="completedDateString") var completedDateString: String?,
    @PrimaryKey(autoGenerate=true) var id: Int = 0
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun completedDate(): LocalDate? {
        return completedDateString?.let { dateString ->
            try {
                dateFormatter.parse(dateString, LocalDate::from)
            } catch (e: Exception) {
                // Handle parsing exception gracefully
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dueTime(): LocalTime? {
        return dueTimeString?.let { timeString ->
            try {
                timeFormatter.parse(timeString, LocalTime::from)
            } catch (e: Exception) {
                // Handle parsing exception gracefully
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isCompleted(): Boolean {
        return completedDate() != null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun imageResource(): Int {
        return if (isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun imageColor(context: Context): Int {
        return if (isCompleted()) purple(context) else black(context)
    }

    private fun purple(context: Context): Int {
        return ContextCompat.getColor(context, R.color.purple)
    }

    private fun black(context: Context): Int {
        return ContextCompat.getColor(context, R.color.black)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        @RequiresApi(Build.VERSION_CODES.O)
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }
}
