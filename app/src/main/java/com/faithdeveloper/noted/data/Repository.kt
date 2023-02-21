package com.faithdeveloper.noted.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object Repository {

    suspend fun signUp(auth: FirebaseAuth, email:String, password:String){
        auth.signInWithEmailAndPassword(email, password).await()
    }
}