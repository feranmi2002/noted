package com.faithdeveloper.noted.models

import com.google.firebase.firestore.DocumentSnapshot

data class PagerResult(
    val lastDocumentSnapshot: DocumentSnapshot?,
    val notes: List<Note>
) {
    constructor() : this(null, listOf())
}