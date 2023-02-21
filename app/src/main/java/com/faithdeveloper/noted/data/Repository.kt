package com.faithdeveloper.noted.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

object Repository {

    suspend fun signUp(auth: FirebaseAuth, email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun verifyEmail(currentUser:FirebaseUser) {
        currentUser.sendEmailVerification().await()
    }
}