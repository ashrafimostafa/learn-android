package com.dev.learnandroid.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dev.learnandroid.R
import com.dev.learnandroid.databinding.FragmentEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_task.*

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEditTaskBinding.bind(view)

        binding.apply {
            editTaskEt.setText(viewModel.taskName)
            editTaskImportant.isChecked = viewModel.taskImportance
            editTaskImportant.jumpDrawablesToCurrentState()
            editTaskDate.isVisible = viewModel.task != null
            editTaskDate.text = "Created Date: ${viewModel.task?.createdDate}"
        }
    }

}
