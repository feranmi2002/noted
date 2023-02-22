package com.faithdeveloper.noted.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User(
    val id:String,
    val email:String,
){
    constructor():this("", "")
}
