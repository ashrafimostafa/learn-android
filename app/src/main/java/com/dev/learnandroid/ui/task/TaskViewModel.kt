package com.dev.learnandroid.ui.task

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
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
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {


    private val taskEventChanel = Channel<TaskEvent>()

    val taskEvent = taskEventChanel.receiveAsFlow()

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferenceFlow = preferencesManager.preferencesFlow

    private val taskFlow = combine(
        searchQuery.asFlow(),
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

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChanel.send(TaskEvent.NavigateToEditTaskFragment(task))
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

    fun addOnAddTaskClick() = viewModelScope.launch {
        taskEventChanel.send(TaskEvent.NavigateToAddTaskFragment)
    }


    sealed class TaskEvent {
        object NavigateToAddTaskFragment : TaskEvent()
        data class NavigateToEditTaskFragment(val task: Task) : TaskEvent()
        data class ShouldUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }
}

