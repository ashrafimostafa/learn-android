package com.dev.learnandroid.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dev.learnandroid.data.local.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)

    val hideCompleted = MutableStateFlow(false)

    private val taskFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ) { query, sortOrder, hideApp ->
        Triple(query, sortOrder, hideApp)
    }.flatMapLatest { (query, sortOrder, hideApp) ->
        taskDao.getTask(query, sortOrder, hideApp)
    }

    val taskList = taskFlow.asLiveData()

}

enum class SortOrder {
    BY_NAME,
    BY_DATE
}