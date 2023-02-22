package com.faithdeveloper.noted.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.util.*


object Util {

    private val USER_DETAILS_UPLOADED = booleanPreferencesKey("user_details_uploaded")
    private const val NOTED_PREFERENCES = "noted_preferences"

    const val DATE = "date"
    private val Context.dataStore by preferencesDataStore(
        name = NOTED_PREFERENCES
    )

    fun formatDate(lastUpdated: Date?): String {
        return ""
    }

    suspend fun Context.userDataUploaded(name: String, dateJoined: Long) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[USER_DETAILS_UPLOADED] = true
        }
    }

    fun Context.getIfUserDataIsUploaded(): Boolean {
        var userDataUploaded: Boolean = false
        dataStore.data.map { preferences ->
            userDataUploaded = preferences[USER_DETAILS_UPLOADED] ?: false
        }
        return userDataUploaded
    }

}