package com.dev.learnandroid.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev.learnandroid.R
import com.dev.learnandroid.databinding.FragmentEditTaskBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.flow.collect

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
            editTaskDate.text = "Created Date: ${viewModel.task?.createdDateFormatted}"

            editTaskEt.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            editTaskImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            editTaskFab.setOnClickListener {
                viewModel.onSaveClicked()
            }

        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditEvent.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTaskEt.clearFocus()
                        setFragmentResult("add_edit_task", bundleOf("result" to event.result))
                        findNavController().popBackStack()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }

            }
        }
    }

}
