package com.faithdeveloper.noted.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.faithdeveloper.noted.data.Repository
import com.faithdeveloper.noted.models.Note
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class NotesListPagingSource(private val repository: Repository, val database: FirebaseFirestore) :
    PagingSource<DocumentSnapshot, Note>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Note>): DocumentSnapshot? {
//        do nothing
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Note> {
        return try {
            val notes = repository.getNotes(database, PAGE_SIZE, params.key)
            val nextKey = if (notes.notes.size <= 30) null
            else notes.lastDocumentSnapshot
            LoadResult.Page(
                data = notes.notes,
                nextKey = nextKey,
                prevKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    companion object {
        const val PAGE_SIZE: Long = 30
    }
}