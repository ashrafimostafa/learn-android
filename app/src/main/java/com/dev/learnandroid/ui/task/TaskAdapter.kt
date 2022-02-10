package com.dev.learnandroid.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev.learnandroid.data.local.Task
import com.dev.learnandroid.databinding.ItemTaskBinding

class TaskAdapter(private val clickListener: OnItemClickListener) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickListener.onItemClicked(getItem(adapterPosition))
                    }
                }

                itemTaskCheck.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickListener.onCheckboxClicked(
                            getItem(adapterPosition),
                            itemTaskCheck.isChecked
                        )
                    }
                }

            }
        }

        fun bind(task: Task) {
            binding.apply {
                itemTaskName.text = task.name
                itemTaskName.paint.isStrikeThruText = task.isChecked
                itemTaskCheck.isChecked = task.isChecked
                itemTaskPriority.isVisible = task.isImportant
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(task: Task)
        fun onCheckboxClicked(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}