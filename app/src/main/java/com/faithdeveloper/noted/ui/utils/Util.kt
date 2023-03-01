package com.faithdeveloper.noted.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object Util {

    private val USER_DETAILS_UPLOADED = booleanPreferencesKey("user_details_uploaded")
    private const val NOTED_PREFERENCES = "noted_preferences"

    private val Context.dataStore by preferencesDataStore(
        name = NOTED_PREFERENCES
    )

    fun formatTime(milliseconds: Long) = String.format(
        "%2d:%2d",
        TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                milliseconds
            )
        )
    )


fun formatDate(lastUpdated: Date?): kotlin.String {
    return SimpleDateFormat.getDateTimeInstance().format(lastUpdated ?: Date())
}

suspend fun Context.userDataUploaded() {
    dataStore.edit { mutablePreferences ->
        mutablePreferences[USER_DETAILS_UPLOADED] = true
    }
}

fun Context.getIfUserDataIsUploaded(): Boolean {
    var userDataUploaded = false
    dataStore.data.map { preferences ->
        userDataUploaded = preferences[USER_DETAILS_UPLOADED] ?: false
    }
    return userDataUploaded
}

    enum class SORT_TYPES{
        LATEST,
        OLDEST
    }

    enum class NOTE_FRAGMENT_FLAGS{
        NORMAL_TOOLBAR,
        ACTION_TOLLBAR,
        SEARCH_TOOLBAR
    }

}