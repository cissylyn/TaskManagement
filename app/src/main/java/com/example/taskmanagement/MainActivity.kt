package com.example.taskmanagement

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),TaskItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((application as TodoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newTaskButton.setOnClickListener{
            NewTaskSheet(taskItem= null).show(supportFragmentManager,"newTaskTag")

        }
setRecyclerView()


        //setContentView(R.layout.activity_main),i commented
    }

    private fun setRecyclerView() {
        val mainActivity = this
        taskViewModel.taskItems.observe(this){
            binding.todoListRecycleView.apply {
                layoutManager=LinearLayoutManager(applicationContext)
                adapter = TaskItemAdapter(it,mainActivity)

            }
        }
    }

    override fun editTaskitem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(supportFragmentManager,"newTaskTag")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
    }


}