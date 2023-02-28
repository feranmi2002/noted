package com.faithdeveloper.noted.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.data.Repository
import com.faithdeveloper.noted.data.Result
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet.Companion.PASSWORD_FLAG
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet.Companion.SIGN_IN_FLAG
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet.Companion.SIGN_UP_FLAG
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet.Companion.VERIFICATION_FLAG
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class OnBoardingViewModel(
    val repository: Repository,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _result: MutableLiveData<Result> = MutableLiveData()
    val result: LiveData<Result> get() = _result

    private val _timer: MutableLiveData<Long> = MutableLiveData()
    val timer: LiveData<Long> get() = _timer

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

    fun verifyEmail(): Result {
        val result: Result = Result.Failure(null)
        viewModelScope.launch {
            try {
                repository.verifyEmail(auth.currentUser!!)
                _result.value = Result.Success(null, VERIFICATION_FLAG)

            } catch (e: Exception) {
                _result.value = Result.Failure(e, VERIFICATION_FLAG)
            }
        }
        return result
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signIn(auth, email, password)
                if (auth.currentUser!!.isEmailVerified) {
                    _result.value = Result.Success(null, SIGN_IN_FLAG)
                } else {
                    _result.value = verifyEmail()
                }
            } catch (e: Exception) {
                _result.postValue(Result.Failure(e, SIGN_IN_FLAG))
            }
        }
    }

    fun startTimer() {
        val countDownTimer = object : CountDownTimer(VERIFICATION_TIME, TIMER_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                _timer.value = millisUntilFinished
            }

            override fun onFinish() {

            }
        }
        countDownTimer.start()
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                repository.forgotPassword(auth, email)
                _result.value = Result.Success(null, PASSWORD_FLAG)
            } catch (e: Exception) {
                _result.value = Result.Failure(e, PASSWORD_FLAG)
            }
        }
    }


    companion object {
        private const val VERIFICATION_TIME = 4500L
        private const val TIMER_INTERVAL = 1000L

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as NotedApplication

                return OnBoardingViewModel(
                    repository = application.repository,
                    auth = application.auth
                ) as T
            }
        }
    }


}