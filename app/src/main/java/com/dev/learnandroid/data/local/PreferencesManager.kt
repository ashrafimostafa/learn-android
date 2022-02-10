package com.dev.learnandroid.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataSore = context.createDataStore("learn_preferences")

    val preferencesFlow = dataSore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "I/O error", exception)
                emit(emptyPreferences())
            }
        }
        .map { preferences ->

            val sortOrder =
                SortOrder.valueOf(preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name)

            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false

            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataSore.edit {
            it[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hide: Boolean) {
        dataSore.edit {
            it[PreferencesKeys.HIDE_COMPLETED] = hide
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

enum class SortOrder {
    BY_NAME,
    BY_DATE
}