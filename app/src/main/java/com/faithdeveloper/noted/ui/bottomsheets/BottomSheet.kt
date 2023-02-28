package com.faithdeveloper.noted.ui.bottomsheets


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.util.PatternsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.faithdeveloper.noted.R
import com.faithdeveloper.noted.data.Result
import com.faithdeveloper.noted.databinding.BottomSheetBinding
import com.faithdeveloper.noted.ui.fragments.OnboardingFragmentDirections
import com.faithdeveloper.noted.ui.utils.Util.formatTime
import com.faithdeveloper.noted.viewmodels.OnBoardingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.util.*

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
        forgotPassword()
        signInWatchers()
        signIn()

    }

    private fun loadSignUpView() {
        binding.signIn.signIn.isVisible = false
        binding.signUp.signUp.isVisible = true
        binding.verification.verification.isVisible = false
        signUpWatchers()
        signUp()

    }

    private fun loadVerification() {
        binding.signIn.signIn.isVisible = false
        binding.signUp.signUp.isVisible = false
        binding.verification.verification.isVisible = true
        verifyEmailRequest()

    }

    private fun loadForgotPassword() {
        binding.signIn.signIn.isVisible = false
        binding.signUp.signUp.isVisible = false
        binding.forgotPassword.forgotPasswordLink.isVisible = true
        forgotPasswordRequest()
    }

    private fun signUpWatchers() {
        binding.signUp.emailLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signUp.continued.isEnabled =
                    s?.trim()?.let {
                        PatternsCompat.EMAIL_ADDRESS.matcher(
                            it
                        ).matches()
                    } == true

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

                binding.signUp.passwordLayout.error = if (s?.trim()!!.count() < PASSWORD_COUNT) {
                    "Password should be more than 6 sharacters"
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

    private fun signIn() {
        binding.signIn.continued.setOnClickListener {
            "Signing you in".showProcessDialog()
            viewModel.signIn(
                binding.signIn.emailLayout.editText?.text!!.trim().toString(),
                binding.signIn.passwordLayout.editText?.text!!.trim().toString()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.result.observe(viewLifecycleOwner) {
            alertDialog?.dismiss()
            when (it) {
                is Result.Success -> when (it.msg) {
                    SIGN_IN_FLAG -> signInSuccessful()
                    VERIFICATION_FLAG -> verificationSuccessful()
                    PASSWORD_FLAG -> passwordSuccessful()
                }

                is Result.Failure -> when (it.msg) {
                    SIGN_UP_FLAG -> signUpFailure(it.data as Exception)
                    SIGN_IN_FLAG -> signInFailure(it.data as Exception)
                    VERIFICATION_FLAG -> verificationFailure()
                    PASSWORD_FLAG -> passwordFailure()
                }
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) {
            when (it.msg) {
                VERIFICATION_FLAG -> if ((it.data as Long) < 1200) {
                    enableVerification()
                } else {
                    binding.verification.timer.text = formatTime(it.data)
                }

                PASSWORD_FLAG -> if ((it.data as Long) < 1200) {
                    enableForgotPassword()
                } else {
                    binding.forgotPassword.passwordTimer.text = formatTime(it.data)
                }
            }
        }
    }

    private fun enableVerification() {
        binding.verification.requestForVerificationLink.isEnabled = true
        binding.verification.timer.isVisible = false
        binding.verification.information.text =
            "Click RESEND to resend verification link to ${viewModel.auth.currentUser!!.email}"
    }

    private fun enableForgotPassword() {
        binding.forgotPassword.requestForPasswordLink.isEnabled = true
        binding.forgotPassword.passwordTimer.isVisible = false
        binding.forgotPassword.passwordTitle.text =
            "Password reset link sent"
    }


    private fun passwordSuccessful() {
        loadForgotPasswordSuccess(binding.signIn.emailLayout.editText?.text!!.trim().toString())

    }

    private fun passwordFailure() {
        "We couldn't send a reset link. Please try again.".messageDialog()
    }


    private fun verificationFailure() {
        loadVerificationFailure()
    }

    private fun signInFailure(e: Exception) {
        when (e) {
            is FirebaseAuthInvalidUserException -> "This account is not registered. Please Sign Up".messageDialog()
            is FirebaseNetworkException -> "Unable to connect. Check your internet connection and try again".messageDialog()
            else -> "Please check correct email and password".messageDialog()

        }

    }

    private fun signUpFailure(e: Exception) {
        when (e) {
            is FirebaseAuthUserCollisionException -> "This email is already in use".messageDialog()
            else -> "Sign up failed".messageDialog()
        }
    }

    private fun loadVerificationFailure() {
        loadVerification()
        binding.verification.title.text = when (flag) {
            SIGN_IN_FLAG -> "Signed In Successfully"
            else -> "Signed Up Successfully"
        }
        binding.verification.information.text =
            "We couldn't verify your email. Click RESEND to try again"
        binding.verification.requestForVerificationLink.isEnabled = true
        binding.verification.timer.isVisible = false

    }

    private fun verificationSuccessful() {
        loadVerificationSuccess(viewModel.auth.currentUser!!.email!!)
    }

    private fun loadVerificationSuccess(email: String) {
        loadVerification()
        binding.verification.information.text = resources.getString(R.string.verification).format(
            Locale.ENGLISH, email
        )
        binding.verification.requestForVerificationLink.isEnabled = false
        binding.verification.title.text = when (flag) {
            SIGN_IN_FLAG -> "Signed In Successfully"
            else -> "Signed Up Successfully"
        }
        viewModel.startTimer(VERIFICATION_FLAG)
    }

    private fun loadForgotPasswordSuccess(email: String) {
        loadForgotPassword()
        binding.forgotPassword.passwordInformation.text =
            resources.getString(R.string.forgot_password_info).format(
                Locale.ENGLISH, email
            )
        binding.forgotPassword.requestForPasswordLink.isEnabled = false
        binding.forgotPassword.passwordTitle.text = "Password Link Sent"
        viewModel.startTimer(PASSWORD_FLAG)
    }

    private fun verifyEmailRequest() {
        binding.verification.requestForVerificationLink.setOnClickListener {
            "Verifying your email...".showProcessDialog()
            viewModel.verifyEmail()
        }
    }

    private fun forgotPasswordRequest() {
        binding.forgotPassword.requestForPasswordLink.setOnClickListener {
            "Sending password reset link to ${
                binding.signIn.emailLayout.editText?.text!!.trim()
            }".showProcessDialog()
            viewModel.forgotPassword(binding.signIn.emailLayout.editText?.text!!.trim().toString())
        }
    }

    private fun signInSuccessful() {
        findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToNotesFragment())
        dismiss()
    }


    private fun forgotPassword() {
        binding.signIn.forgotPassword.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(
                    binding.signIn.emailLayout.editText?.text!!.trim().toString()
                ).matches()
            ) {
                Snackbar.make(
                    binding.signIn.forgotPassword,
                    "Enter valid email",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                "Sending password reset link to ${
                    binding.signIn.emailLayout.editText?.text!!.trim()
                }".showProcessDialog()
                viewModel.forgotPassword(
                    binding.signIn.emailLayout.editText?.text!!.trim().toString()
                )
            }
        }
    }

    private fun String.showProcessDialog() {
        val materialDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setMessage(this)
            .setCancelable(false)
        alertDialog = materialDialogBuilder.create()
        alertDialog?.show()
    }

    private fun String.messageDialog() {
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
        const val PASSWORD_FLAG = "password"
        fun getButtomSheet(flag: String) = BottomSheet(flag)
    }
}