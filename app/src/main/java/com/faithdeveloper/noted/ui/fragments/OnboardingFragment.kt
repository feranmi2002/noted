package com.faithdeveloper.noted.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faithdeveloper.noted.MainActivity
import com.faithdeveloper.noted.data.viewmodels.OnBoardingViewModel
import com.faithdeveloper.noted.databinding.OnboardingScreenBinding

class OnboardingFragment : Fragment() {
    private var _binding: OnboardingScreenBinding? = null
    private lateinit var activity: MainActivity
    private val viewModel:OnBoardingViewModel by viewModels()
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

//    private fun presentView(){
//        binding.signUp.
//    }

    private fun signup(){
        binding.signUp.setOnClickListener {

        }
    }

    private fun signIn(){

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}