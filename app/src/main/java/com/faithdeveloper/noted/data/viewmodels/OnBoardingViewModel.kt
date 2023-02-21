package com.faithdeveloper.noted.data.viewmodels

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.data.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    val repository: Repository,
    val auth: FirebaseAuth,
    database: FirebaseFirestore
) : ViewModel() {

    private val process: String = SIGN_UP
    private val _result: MutableLiveData<Boolean> = MutableLiveData()
    val result: LiveData<Boolean> get() = _result

    private fun signup(email: String, password: String) {
        try {
            viewModelScope.launch {
                repository.signUp(auth, email, password)
                _result.value = true
            }
        } catch (e: Exception) {
//            Failed to sign up
            _result.value = false
        }
    }

    fun checkIfUserExists() = auth.currentUser

    companion object {
        const val SIGN_UP = "sign_up"

        //        val Factory(repository: Repository, auth: FirebaseAuth, database:FirebaseFirestore) = object :ViewModelProvider.Factory
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val repository = (this[APPLICATION_KEY] as NotedApplication).repository
                val auth = (this[APPLICATION_KEY] as NotedApplication).auth
                val database = (this[APPLICATION_KEY] as NotedApplication).database
                OnBoardingViewModel(
                    repository = repository,
                    auth = auth,
                    database = database
                )
            }
        }

    }

}