package com.example.taskmanagement

import android.content.Context
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagement.databinding.TaskItemCellBinding
import java.time.format.DateTimeFormatter

class TaskItemViewHolder(
    private val context:Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: TaskItemClickListener
):RecyclerView.ViewHolder(binding.root)
{
    @RequiresApi(Build.VERSION_CODES.O)
    private  val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindTaskItem(taskItem: TaskItem){
        binding.name.text=taskItem.name

        // Check if start date is available
        if (!taskItem.startDateString.isNullOrEmpty()) {
            binding.startDateTextView.text = "Start Date: ${taskItem.startDateString}"
        } else {
            binding.startDateTextView.text = "Start Date: N/A"
        }

        // Check if end date is available
        if (!taskItem.endDateString.isNullOrEmpty()) {
            binding.endDateTextView.text = "End Date: ${taskItem.endDateString}"
        } else {
            binding.endDateTextView.text = "End Date: N/A"
        }


        if (taskItem.isCompleted()){
            binding.name.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
        }
        binding.completeButton.setImageResource(taskItem.imageResource())
        binding.completeButton.setColorFilter(taskItem.imageColor(context))

        binding.completeButton.setOnClickListener{
            clickListener.completeTaskItem(taskItem)
        }
        binding.btnDelete.setOnClickListener {
            clickListener.deleteTaskItem(taskItem)
        }
        binding.taskCellContainer.setOnClickListener{
            clickListener.editTaskitem(taskItem)
        }
        if(taskItem.dueTime() != null) {
            binding.dueTime.text = timeFormat.format(taskItem.dueTime())
        }
        else {
            binding.dueTime.text = ""
        }
    }
}