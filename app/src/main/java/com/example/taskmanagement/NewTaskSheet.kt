package com.example.taskmanagement

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagement.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueTime: LocalTime? = null
    private var startDate: Long? = null
    private var endDate: Long? = null
    private var startDateString: String? = null
    private var endDateString: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewTaskSheetBinding.bind(view)

        if (taskItem != null) {
            binding.taskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            if (taskItem!!.dueTime() != null) {
                dueTime = taskItem!!.dueTime()!!
                updateTimeButtonText()
            }
            if (!taskItem!!.startDateString.isNullOrEmpty()) {
                binding.startDateButton.text = taskItem!!.startDateString
            }

            if (!taskItem!!.endDateString.isNullOrEmpty()) {
                binding.endDateButton.text = taskItem!!.endDateString
            }
        } else {
            binding.taskTitle.text = "New Task"
        }

        taskViewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]



        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }
        binding.saveButton.setOnClickListener {
            saveAction()
        }



        binding.startDateButton.setOnClickListener {
            openStartDatePicker()
        }

        binding.endDateButton.setOnClickListener {
            openEndDatePicker()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }
        val dialog = TimePickerDialog(
            requireActivity(),
            listener,
            dueTime?.hour ?: LocalTime.now().hour,
            dueTime?.minute ?: LocalTime.now().minute,
            true
        )
        dialog.setTitle("Task Due")
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeButtonText() {
        binding.timePickerButton.text = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun openStartDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Start Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            startDate = selection
            startDateString = formatDate(selection)
            updateDateButtonText()
        }

        datePicker.show(parentFragmentManager, "StartDatePicker")
    }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun openEndDatePicker() {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select End Date")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                endDate = selection
                endDateString = formatDate(selection)
                updateDateButtonText()
            }

            datePicker.show(parentFragmentManager, "EndDatePicker")
        }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateButtonText() {
        binding.startDateButton.text = startDate?.let { formatDate(it) } ?: "Select Start Date"
        binding.endDateButton.text = endDate?.let { formatDate(it) } ?: "Select End Date"
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAction() {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()

        // Add the description parameter
        //val description = "Description: $name - $desc"
        val dueTimeString = dueTime?.let { TaskItem.timeFormatter.format(it) }

        Log.d("NewTaskSheet", "Name: $name, Desc: $desc, Due Time String: $dueTimeString")

        if (taskItem == null) {
//            val newTask = TaskItem(name, desc,dueTimeString , null)

            val newTask = TaskItem(name=name, desc=desc, dueTimeString, null, startDateString=startDateString, endDateString=endDateString)
            taskViewModel.addTaskItem(newTask)
        } else {
            taskItem!!.name = name
            taskItem!!.desc = desc
            taskItem!!.dueTimeString = dueTimeString
            taskItem!!.startDateString = startDateString
            taskItem!!.endDateString = endDateString
            taskViewModel.updateTaskItem(taskItem!!)
        }
        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_task_sheet, container, false)
    }
}
//i  have changed this code