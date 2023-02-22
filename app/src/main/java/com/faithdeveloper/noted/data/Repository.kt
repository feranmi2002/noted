package com.faithdeveloper.noted.data

import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.models.PagerResult
import com.faithdeveloper.noted.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

object Repository {
    private const val USER_IDS = "users_ids"
    private const val USERS = "users"
    private const val NOTES = "notes"
    private const val LAST_UPDATED = "lastUpdated"

    suspend fun signUp(auth: FirebaseAuth, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun verifyEmail(currentUser: FirebaseUser) {
        currentUser.sendEmailVerification().await()
    }

    suspend fun signIn(auth: FirebaseAuth, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun forgotPassword(auth: FirebaseAuth, email: String) {
        auth.sendPasswordResetEmail(email).await()

    }

    suspend fun getNotes(
        database: FirebaseFirestore,
        size: Long,
        lastDocumentSnapshot: DocumentSnapshot?,
        userUid: String
    ): PagerResult {
        val query = if (lastDocumentSnapshot == null) {
            database.collection(USERS).document(userUid).collection(NOTES)
                .orderBy(LAST_UPDATED, Query.Direction.DESCENDING)
                .limit(size)
        } else {
            database.collection(USERS).document(userUid).collection(NOTES)
                .orderBy(LAST_UPDATED, Query.Direction.DESCENDING)
                .limit(size).startAfter(lastDocumentSnapshot)
        }
        val result = query.get().await()
        return PagerResult(result.last(), formatSnapshots(result))
    }

    private fun formatSnapshots(documents: QuerySnapshot) =
        documents.map { documentSnapshot ->
            documentSnapshot.toObject<Note>()
        }

    fun uploadUserData(
        userUid: String,
        email: String,
        database: FirebaseFirestore
    ) {
        database.collection(USERS).document(userUid).set(
            User(userUid, email)
        )
    }
}