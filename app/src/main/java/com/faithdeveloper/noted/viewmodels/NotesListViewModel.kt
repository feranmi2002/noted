package com.faithdeveloper.noted.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.*
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.data.Repository
import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.ui.paging.NotesListPagingSource
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("UNCHECKED_CAST")
class NotesListViewModel(val repository: Repository, val database: FirebaseFirestore) :
    ViewModel() {
    private val _notes = loadNotes()
    val notes: LiveData<PagingData<Note>> get() = _notes


    private fun loadNotes() = Pager(
        PagingConfig(
            pageSize = NotesListPagingSource.PAGE_SIZE.toInt()
        ), pagingSourceFactory = {
            NotesListPagingSource(repository, database)
        }, initialKey = null
    ).liveData.cachedIn(viewModelScope)

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NotedApplication

                return NotesListViewModel(
                    repository = application.repository,
                    database = application.database
                ) as T
            }
        }
    }

}