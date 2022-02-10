package com.dev.learnandroid.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.learnandroid.data.local.Task
import com.dev.learnandroid.data.local.TaskDao
import com.dev.learnandroid.ui.TASK_ADDED
import com.dev.learnandroid.ui.TASK_UPDATED
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {


    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.isImportant ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addEditTaskEventChanel = Channel<AddEditTaskEvent>()
    val addEditEvent = addEditTaskEventChanel.receiveAsFlow()

    fun onSaveClicked() {
        if (taskName.isBlank()) {
            //show snackbar
            viewModelScope.launch {
                addEditTaskEventChanel
                    .send(AddEditTaskEvent.ShowInvalidInputMessage("Name can not be empty"))
            }
            return
        }

        if (task != null) {
            val updatedTask = task.copy(
                name = taskName,
                isImportant = taskImportance,
                createdDate = System.currentTimeMillis()
            )
            viewModelScope.launch {
                taskDao.updateTask(updatedTask)
                addEditTaskEventChanel
                    .send(AddEditTaskEvent.NavigateBackWithResult(TASK_UPDATED))
            }

        } else {
            val newTask = Task(taskName, false, taskImportance)
            viewModelScope.launch {
                taskDao.insertTask(newTask)
                addEditTaskEventChanel
                    .send(AddEditTaskEvent.NavigateBackWithResult(TASK_ADDED))
            }

        }
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val message: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }


}