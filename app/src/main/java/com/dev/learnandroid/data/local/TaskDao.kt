package com.dev.learnandroid.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTask(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getTaskSortByName(query, hideCompleted)
            SortOrder.BY_DATE -> getTaskSortByDate(query, hideCompleted)
        }

    @Query("SELECT * FROM tasktable WHERE (isChecked != :hideCompleted OR isChecked= 0 ) AND name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, name")
    fun getTaskSortByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasktable WHERE (isChecked != :hideCompleted OR isChecked= 0 ) AND name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, createdDate")
    fun getTaskSortByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}