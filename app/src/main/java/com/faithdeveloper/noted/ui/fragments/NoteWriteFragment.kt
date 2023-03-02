package com.faithdeveloper.noted.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.faithdeveloper.noted.R
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.databinding.DeleteDialogBinding
import com.faithdeveloper.noted.databinding.WriteNoteBinding
import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.ui.utils.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class NoteWriteFragment : Fragment() {
    private var _binding: WriteNoteBinding? = null
    private var backPressedCallback: OnBackPressedCallback? = null
    private lateinit var application: NotedApplication
    private lateinit var note: Note

    private var saveNote: Boolean = true
    private var onBackPressed = false


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = arguments?.getParcelable(NOTE)!!
        application = requireActivity().application as NotedApplication

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = WriteNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (saveNote) {
                    onBackPressed = if (note.id.isEmpty()) {
                        saveNote()
                        true
                    } else {
                        updateNote()
                        true
                    }
                }
                findNavController().popBackStack()
            }
        loadViews()
        back()
        delete()
        shareNote()
    }

    private fun updateNote() {
        if (binding.noteTitle.text.toString().isNotEmpty() || binding.notes.text.toString()
                .isNotEmpty()
        ) {
            application.update(
                note.apply {
                    title = binding.noteTitle.text.toString()
                    note = binding.notes.text.toString()
                    lastUpdated = null
                }
            )
            Snackbar.make(binding.root, getString(R.string.note_updated), Snackbar.LENGTH_SHORT).show()
        }else{
            Snackbar.make(binding.root, getString(R.string.note_discarded), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveNote() {
        if (binding.noteTitle.text.toString().isNotEmpty() || binding.notes.text.toString()
                .isNotEmpty()
        ) {
            application.save(
                note.apply {
                    title = binding.noteTitle.text.toString()
                    note = binding.notes.text.toString()
                    dateCreated = null
                    lastUpdated = null
                }
            )
            Snackbar.make(binding.root, getString(R.string.note_saved), Snackbar.LENGTH_SHORT).show()
        }else{
            Snackbar.make(binding.root, getString(R.string.note_discarded), Snackbar.LENGTH_SHORT).show()
        }
    }


    private fun loadViews() {
        binding.time.text = Util.formatDate(note.lastUpdated)
        binding.noteTitle.setText(note.title)
        binding.notes.setText(note.note)
    }

    private fun delete() {
        binding.delete.setOnClickListener {
            var dialog: AlertDialog? = null
            val deleteDialogBinding =
                DeleteDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            deleteDialogBinding.cancelButton.setOnClickListener {
                dialog?.dismiss()
            }
            deleteDialogBinding.confirmButton.setOnClickListener {
                dialog?.dismiss()
                saveNote = false
                if (note.trackingId.isEmpty()) backPressedCallback?.handleOnBackPressed()
                else {
                    application.deleteNote(listOf(note.trackingId))
                    backPressedCallback?.handleOnBackPressed()
                }
            }

            dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(deleteDialogBinding.root).create()
            dialog.show()
        }
    }

    private fun shareNote() {
        binding.share.setOnClickListener {
            if (binding.notes.text!!.isNotEmpty()) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${binding.noteTitle.text.toString()}\n${binding.notes.text.toString()}"
                    )
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, null))
            } else {
                Snackbar.make(binding.root, "Can't share empty note", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun back() {
        binding.back.setOnClickListener {
            backPressedCallback?.handleOnBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        backPressedCallback = null
    }

    override fun onPause() {
        super.onPause()
        if (!onBackPressed && saveNote) {
            if (note.trackingId.isEmpty()) saveNote()
            else updateNote()
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressed = false
    }

    companion object {
        const val NOTE = "note"
    }
}