package com.faithdeveloper.noted.ui.bottomsheets


import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.util.PatternsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.faithdeveloper.noted.data.Result
import com.faithdeveloper.noted.data.viewmodels.OnBoardingViewModel
import com.faithdeveloper.noted.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class BottomSheet(private val flag: String) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnBoardingViewModel by viewModels { OnBoardingViewModel.Factory }

    private var alertDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadView()
        observeViewModel()
        signUp()
        signIn()


    }

    private fun loadView() {
        when (flag) {
            SIGN_IN_FLAG -> loadSignInView()
            SIGN_UP_FLAG -> loadSignUpView()
            VERIFICATION_FLAG -> loadVerification()
        }
    }

    private fun loadSignInView() {
        binding.signIn.signIn.isVisible = true
        binding.signUp.signUp.isVisible = false
        binding.verification.verification.isVisible = false
        signInWatchers()

    }

    private fun loadSignUpView() {
        binding.signIn.signIn.isVisible = false
        binding.signUp.signUp.isVisible = true
        binding.verification.verification.isVisible = false
        signUpWatchers()

    }

    private fun loadVerification() {
        binding.signIn.signIn.isVisible = false
        binding.signUp.signUp.isVisible = false
        binding.verification.verification.isVisible = true

    }

    private fun signUpWatchers() {
        binding.signUp.userNameLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.signUp.continued.isEnabled = (s?.trim()!!.isNotEmpty())

                        && PatternsCompat.EMAIL_ADDRESS.matcher(
                    binding.signUp.passwordLayout.editText?.text!!.trim()
                ).matches()

                        && binding.signUp.passwordLayout.editText?.text!!.trim().count() > 6

                binding.signUp.userNameLayout.error = if (s.trim().isEmpty()) {
                    "Name cannot be empty"
                } else {
                    null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                do nothing
            }

            override fun afterTextChanged(s: Editable?) {
//                do nothing
            }
        })


        binding.signUp.emailLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signUp.continued.isEnabled =
                    s?.trim()?.let {
                        PatternsCompat.EMAIL_ADDRESS.matcher(
                            it
                        ).matches()
                    } == true

                            && binding.signUp.userNameLayout.editText?.text!!.trim().isNotEmpty()

                            && binding.signUp.passwordLayout.editText?.text!!.trim()
                        .count() > PASSWORD_COUNT

                binding.signUp.emailLayout.error = if (s?.trim()!!.isEmpty()) {
                    "Email is invalid"
                } else {
                    null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                do nothing
            }

            override fun afterTextChanged(s: Editable?) {
//                do nothing
            }
        })


        binding.signUp.passwordLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signUp.continued.isEnabled = s?.trim()!!.count() > PASSWORD_COUNT

                        &&
                        PatternsCompat.EMAIL_ADDRESS.matcher(
                            binding.signUp.emailLayout.editText?.text!!.trim()
                        ).matches()

                        && binding.signUp.userNameLayout.editText?.text!!.trim().isNotEmpty()


                binding.signUp.passwordLayout.error = if (s?.trim()!!.count() < PASSWORD_COUNT) {
                    "Password is weak"
                } else {
                    null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                do nothing
            }

            override fun afterTextChanged(s: Editable?) {
//                do nothing
            }
        })
    }

    private fun signInWatchers() {
        binding.signIn.emailLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signIn.continued.isEnabled =
                    s?.trim()?.let {
                        PatternsCompat.EMAIL_ADDRESS.matcher(
                            it
                        ).matches()
                    } == true

                            && binding.signIn.passwordLayout.editText?.text!!.trim()
                        .count() > PASSWORD_COUNT

                binding.signIn.emailLayout.error = if (s?.trim()!!.isEmpty()) {
                    "Email is invalid"
                } else {
                    null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                do nothing
            }

            override fun afterTextChanged(s: Editable?) {
//                do nothing
            }
        })

        binding.signIn.passwordLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signIn.continued.isEnabled = s?.trim()!!.count() > PASSWORD_COUNT

                        &&
                        PatternsCompat.EMAIL_ADDRESS.matcher(
                            binding.signIn.emailLayout.editText?.text!!.trim()
                        ).matches()


                binding.signIn.passwordLayout.error = if (s?.trim()!!.count() < PASSWORD_COUNT) {
                    "Password is weak"
                } else {
                    null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                do nothing
            }

            override fun afterTextChanged(s: Editable?) {
//                do nothing
            }
        })
    }

    private fun signUp() {
        binding.signUp.continued.setOnClickListener {
            "Signing you up..".showProcessDialog()
            viewModel.signUp(
                binding.signUp.emailLayout.editText?.text!!.trim().toString(),
                binding.signUp.passwordLayout.editText?.text!!.trim().toString()
            )
        }
    }

    private fun signIn(){
        binding.signIn.continued.setOnClickListener {
            "Signing you in".showProcessDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.result.observe(viewLifecycleOwner) {
            alertDialog?.dismiss()
            when (it) {
                is Result.Success -> when (it.msg) {
                    SIGN_UP_FLAG -> signUpSuccessful()
                    SIGN_IN_FLAG -> signInSuccessful()
                    VERIFICATION_FLAG -> verificationSuccessful()
                }

                is Result.Failure -> when (it.msg) {
                    SIGN_UP_FLAG -> signUpFailure(it.data as Exception)
                    SIGN_IN_FLAG -> signInFailure()
                    VERIFICATION_FLAG -> verificationFailure()
                }
            }
        }
    }


    private fun verificationFailure() {

    }

    private fun signInFailure() {
        "Sign in failed".failureDialog()
    }

    private fun signUpFailure(e:Exception) {
        when(e){
            is FirebaseAuthUserCollisionException ->  "This email is already in use".failureDialog()
            else -> "Sign up failed".failureDialog()
        }
    }

    private fun verificationSuccessful() {

    }

    private fun signInSuccessful() {

    }

    private fun signUpSuccessful() {
        loadVerification()
    }

    private fun String.showProcessDialog() {
        val materialDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setMessage(this)
        alertDialog = materialDialogBuilder.create()
        alertDialog?.show()
    }

    private fun String.failureDialog() {
        val materialDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setMessage(this)
            .setPositiveButton("OK") { dialog, which ->
//                do nothing
            }

        alertDialog = materialDialogBuilder.create()
        alertDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val SIGN_UP_FLAG = "sign_up"
        const val SIGN_IN_FLAG = "sign_in"
        const val VERIFICATION_FLAG = "verification"
        const val PASSWORD_COUNT = 6
        fun getButtomSheet(flag: String) = BottomSheet(flag)
    }
}