package com.dev.learnandroid.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dev.learnandroid.data.local.PreferencesManager
import com.dev.learnandroid.data.local.SortOrder
import com.dev.learnandroid.data.local.Task
import com.dev.learnandroid.data.local.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val taskEventChanel = Channel<TaskEvent>()

    val taskEvent = taskEventChanel.receiveAsFlow()

    val searchQuery = MutableStateFlow("")

    val preferenceFlow = preferencesManager.preferencesFlow

    private val taskFlow = combine(
        searchQuery,
        preferenceFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTask(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val taskList = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) =
        viewModelScope.launch { preferencesManager.updateSortOrder(sortOrder) }

    fun onHideCompleteSelected(hideCompleted: Boolean) =
        viewModelScope.launch { preferencesManager.updateHideCompleted(hideCompleted) }

    fun onTaskSelected(task: Task) {

    }

    fun onTaskCheckedChange(task: Task, checked: Boolean) =
        viewModelScope.launch { taskDao.updateTask(task.copy(isChecked = checked)) }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.deleteTask(task)
        taskEventChanel.send(TaskEvent.ShouldUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insertTask(task)
    }


    sealed class TaskEvent {
        data class ShouldUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }
}

