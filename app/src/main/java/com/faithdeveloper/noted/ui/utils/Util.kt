package com.faithdeveloper.noted.ui.utils

import android.content.Context
import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


object Util {

    private val USER_DETAILS_UPLOADED = booleanPreferencesKey("user_details_uploaded")
    private const val NOTED_PREFERENCES = "noted_preferences"

    private val Context.dataStore by preferencesDataStore(
        name = NOTED_PREFERENCES
    )


    fun formatDate(lastUpdated: Date?): String {
        return SimpleDateFormat.getDateTimeInstance().format(lastUpdated?: Date())
    }

    fun getDateTime(): String {
        val result: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            LocalDateTime.now().format(formatter)
        } else {
            val date1 = Date()
            SimpleDateFormat.getDateTimeInstance().format(date1)
        }
        return result

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

}