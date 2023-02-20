package com.faithdeveloper.noted.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.faithdeveloper.noted.databinding.NotesListBinding
import com.faithdeveloper.noted.databinding.OnboardingScreenBinding
import com.faithdeveloper.noted.databinding.WriteNoteBinding


class NoteWriteFragment : Fragment() {
    private var _binding: WriteNoteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = WriteNoteBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}