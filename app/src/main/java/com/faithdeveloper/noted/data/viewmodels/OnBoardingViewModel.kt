package com.faithdeveloper.noted.data.viewmodels

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.data.Repository
import com.faithdeveloper.noted.data.Result
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet.Companion.SIGN_UP_FLAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class OnBoardingViewModel(
    val repository: Repository,
    val auth: FirebaseAuth,
    database: FirebaseFirestore
) : ViewModel() {

    private val _result: MutableLiveData<Result> = MutableLiveData()
    val result: LiveData<Result> get() = _result

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signUp(auth, email, password)
//                successful sign up
                _result.value = verifyEmail()
            } catch (e: Exception) {
//            Failed to sign up
                    _result.value = Result.Failure(e, SIGN_UP_FLAG)
            }
        }
    }

    private fun verifyEmail(): Result {
        var result: Result = Result.Failure(null)
        viewModelScope.launch {
            result = try {
                repository.verifyEmail(auth.currentUser!!)
                Result.Success(null)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
        return result
    }


    companion object {
        const val SIGN_UP = "sign_up"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as NotedApplication

                return OnBoardingViewModel(
                    repository = application.repository,
                    auth = application.auth,
                    database = application.database
                ) as T
            }
        }
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val savedStateHandle = createSavedStateHandle()
//                val repository = (this[APPLICATION_KEY] as NotedApplication).repository
//                val auth = (this[APPLICATION_KEY] as NotedApplication).auth
//                val database = (this[APPLICATION_KEY] as NotedApplication).database
//                OnBoardingViewModel(
//                    repository = repository,
//                    auth = auth,
//                    database = database
//                )
//            }
//        }
//
    }

}