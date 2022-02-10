package com.dev.learnandroid.ui.deletealldialog

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.dev.learnandroid.data.local.TaskDao
import com.dev.learnandroid.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteCheckedTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClicked() = applicationScope.launch {
        taskDao.deleteCheckedTask()
    }
}