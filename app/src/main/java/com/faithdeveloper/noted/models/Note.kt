package com.faithdeveloper.noted.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class Note(
    var id:String,
    @ServerTimestamp
    var dateCreated :Date?,
    @ServerTimestamp
    var lastUpdated: Date?,
    var title:String,
    var note:String,
    var trackingId:String
): Parcelable{
    constructor():this("", null, null, "", "", "")
}
