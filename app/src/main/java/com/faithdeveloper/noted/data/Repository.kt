package com.faithdeveloper.noted.data

import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.models.PagerResult
import com.faithdeveloper.noted.models.User
import com.faithdeveloper.noted.ui.utils.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

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
        userUid: String,
        sortType: Util.SORT_TYPES
    ): PagerResult {
        val query = if (lastDocumentSnapshot == null) {
            database.collection(USERS).document(userUid).collection(NOTES)
                .orderBy(
                    LAST_UPDATED, when (sortType) {
                        Util.SORT_TYPES.LATEST -> Query.Direction.DESCENDING
                        else -> Query.Direction.ASCENDING
                    }
                )
                .limit(size)
        } else {
            database.collection(USERS).document(userUid).collection(NOTES)
                .orderBy(
                    LAST_UPDATED, when (sortType) {
                        Util.SORT_TYPES.LATEST -> Query.Direction.DESCENDING
                        else -> Query.Direction.ASCENDING
                    }
                )
                .limit(size).startAfter(lastDocumentSnapshot)
        }
        val result = query.get().await()
        return PagerResult(
            if (!result.isEmpty) result.last()
            else null, if (!result.isEmpty) formatSnapshots(result)
            else listOf()
        )
    }

    private fun formatSnapshots(documents: QuerySnapshot) =
        documents.map { documentSnapshot ->
            val note = documentSnapshot.toObject<Note>()
            note.apply {
                trackingId = documentSnapshot.id
            }
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

    fun save(userUid: String, note: Note, database: FirebaseFirestore) {
        database.collection(USERS).document(userUid).collection(NOTES).add(note.apply {
            if (id.isEmpty()) id = userUid + System.currentTimeMillis()
        })
    }

    fun deleteNotes(userUid: String, idOfNotes: List<String>, database: FirebaseFirestore) {
        idOfNotes.onEach {
            database.collection(USERS).document(userUid).collection(NOTES).document(it).delete()
        }
    }

    fun update(uid: String, note: Note, database: FirebaseFirestore) {
        database.collection(USERS).document(uid).collection(NOTES).document(note.trackingId).update(
            mapOf(
                "title" to note.title,
                "note" to note.note,
                "lastUpdated" to Date()
            )
        )
    }
}