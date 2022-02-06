package com.dev.learnandroid.ui.task

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dev.learnandroid.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task) {

    private val viewModel :TaskViewModel by viewModels()


}