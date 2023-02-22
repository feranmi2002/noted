package com.faithdeveloper.noted.data

import android.app.Application
import com.faithdeveloper.noted.models.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotedApplication : Application() {
    private lateinit var _auth: FirebaseAuth
    val auth get() = _auth
    private lateinit var _database: FirebaseFirestore
    val database get() = _database
    private lateinit var _repository: Repository
    val repository get() = _repository
    override fun onCreate() {
        super.onCreate()
        _auth = Firebase.auth
        _database = Firebase.firestore
        _repository = Repository
    }

    fun save(note: Note) {
        repository.save(auth.currentUser!!.uid, note,  database)
    }
}