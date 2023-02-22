package com.faithdeveloper.noted.models

data class Note(
    val id:String,
    val dateCreated :String,
    val lastUpdated:String,
    val title:String,
    val note:String
){
    constructor():this("", "", "", "", "")
}
