package com.faithdeveloper.noted.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faithdeveloper.noted.MainActivity
import com.faithdeveloper.noted.viewmodels.OnBoardingViewModel
import com.faithdeveloper.noted.databinding.OnboardingScreenBinding
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet.Companion.SIGN_IN_FLAG
import com.faithdeveloper.noted.ui.bottomsheets.BottomSheet.Companion.SIGN_UP_FLAG

class OnboardingFragment : Fragment() {
    private var _binding: OnboardingScreenBinding? = null
    private lateinit var activity: MainActivity
    private val viewModel: OnBoardingViewModel by viewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = OnboardingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUp()
        signIn()
    }

    private fun signUp(){
        binding.signUp.setOnClickListener {
            val bottomSheet = BottomSheet.getButtomSheet(SIGN_UP_FLAG)
            bottomSheet.show(requireActivity().supportFragmentManager, "BottomSheet")
        }
    }

    private fun signIn(){
        binding.signIn.setOnClickListener {
            val bottomSheet = BottomSheet.getButtomSheet(SIGN_IN_FLAG)
            bottomSheet.show(requireActivity().supportFragmentManager, "BottomSheet")
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}