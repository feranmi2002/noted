package com.faithdeveloper.noted.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.databinding.WriteNoteBinding
import com.faithdeveloper.noted.ui.utils.Util
import java.util.Date


class NoteWriteFragment : Fragment() {
    private var _binding: WriteNoteBinding? = null
    private var backPressedCallback: OnBackPressedCallback? = null
    private lateinit var application:NotedApplication
    private lateinit var userUid:String


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userUid = arguments?.getString(USER_UID)!!
        application = requireActivity().application as NotedApplication
        backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            saveNote()
            findNavController().popBackStack()
        }
    }

    private fun saveNote() {
        if (binding.noteTitle.text.toString().isNotEmpty() || binding.notes.text.toString().isNotEmpty()){
            application.save(
                userUid,
                binding.noteTitle.text.toString(),
                binding.notes.text.toString()
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = WriteNoteBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViews()
        back()
    }


    private fun loadViews(){
        binding.time.text = Util.getDateTime()
    }

    private fun back(){
        binding.back.setOnClickListener {
            backPressedCallback?.handleOnBackPressed()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        backPressedCallback = null
    }
    companion object{
        const val USER_UID = "user_uid"
    }
}