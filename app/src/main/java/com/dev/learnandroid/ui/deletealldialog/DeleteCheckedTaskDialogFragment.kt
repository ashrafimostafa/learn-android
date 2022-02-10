package com.dev.learnandroid.ui.deletealldialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCheckedTaskDialogFragment : DialogFragment() {

    private val viewModel: DeleteCheckedTaskViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletation")
            .setMessage("Do you want to delete all checked tasks?")
            .setNegativeButton("no", null)
            .setPositiveButton("yes") { _, _ ->
                viewModel.onConfirmClicked()
            }
            .create()
}