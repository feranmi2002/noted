package com.faithdeveloper.noted.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Note(
    val id:String,
    @ServerTimestamp
    val dateCreated :Date?,
    @ServerTimestamp
    val lastUpdated: Date?,
    val title:String,
    val note:String
){
    constructor():this("", null, null, "", "")
}
