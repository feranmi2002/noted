package com.faithdeveloper.noted.viewmodels

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.*
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.data.Repository
import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.ui.paging.NotesListPagingSource
import com.faithdeveloper.noted.ui.utils.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class NotesListViewModel(
    val auth: FirebaseAuth,
    val repository: Repository,
    val database: FirebaseFirestore
) :
    ViewModel() {
    private var sortType: Util.SORT_TYPES = Util.SORT_TYPES.LATEST
    private val _notes = loadNotes()
    val notes: LiveData<PagingData<Note>> get() = _notes



    private fun loadNotes() = Pager(
        PagingConfig(
            pageSize = NotesListPagingSource.PAGE_SIZE.toInt()
        ), pagingSourceFactory = {
            NotesListPagingSource(repository, database, auth.currentUser!!.uid, sortType)
        }, initialKey = null
    ).liveData.cachedIn(viewModelScope)

    fun uploadUserData() {
        viewModelScope.launch {
            try {
                repository.uploadUserData(
                    auth.currentUser!!.uid,
                    auth.currentUser!!.email!!,
                    database
                )
            } catch (e: Exception) {
//              no exception expected
            }
        }
    }

    fun setSortType(sortType: Util.SORT_TYPES) {
        this.sortType = sortType
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NotedApplication

                return NotesListViewModel(
                    auth = application.auth,
                    repository = application.repository,
                    database = application.database
                ) as T
            }
        }
    }
}