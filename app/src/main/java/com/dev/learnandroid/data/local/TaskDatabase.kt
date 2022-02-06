package com.dev.learnandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dev.learnandroid.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao


    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            //dummy data
            val dao = database.get().taskDao()
            applicationScope.launch {
                dao.insertTask(Task("Task 1"))
                dao.insertTask(Task("Task 2"))
                dao.insertTask(Task("Task 3"))
                dao.insertTask(Task("Task 4"))
                dao.insertTask(Task("Task 5"))
                dao.insertTask(Task("Task 6"))
                dao.insertTask(Task("Task 7"))
            }
        }
    }

}