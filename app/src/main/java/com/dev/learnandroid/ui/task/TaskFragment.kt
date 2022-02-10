package com.dev.learnandroid.ui.task

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.learnandroid.R
import com.dev.learnandroid.data.local.SortOrder
import com.dev.learnandroid.data.local.Task
import com.dev.learnandroid.databinding.FragmentTaskBinding
import com.dev.learnandroid.util.onQueryTextChange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task) , TaskAdapter.OnItemClickListener {

    private val viewModel: TaskViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskBinding.bind(view)

        val taskAdapter = TaskAdapter(this)

        binding.apply {
            taskList.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.taskList.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }

        setHasOptionsMenu(true)
    }

    override fun onItemClicked(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckboxClicked(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChange(task,isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task, menu)

        val searchItem = menu.findItem(R.id.menu_task_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.onQueryTextChange { query ->
            viewModel.searchQuery.value = query
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.menu_task_hide_complete_task).isChecked =
                viewModel.preferenceFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_task_sort_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.menu_task_sort_date -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.menu_task_hide_complete_task -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompleteSelected(item.isChecked)
                true
            }
            R.id.menu_task_delete_all_completed_task -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}