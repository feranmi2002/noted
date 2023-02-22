package com.faithdeveloper.noted.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.faithdeveloper.noted.databinding.NotesListBinding
import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.ui.adapters.NotePagingAdapter
import com.faithdeveloper.noted.ui.utils.Util.getIfUserDataIsUploaded
import com.faithdeveloper.noted.ui.utils.Util.userDataUploaded
import com.faithdeveloper.noted.viewmodels.NotesListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class NotesFragment : Fragment() {
    private var _binding: NotesListBinding? = null
    private lateinit var adapter: NotePagingAdapter
    private val viewModel: NotesListViewModel by viewModels { NotesListViewModel.Factory }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = NotePagingAdapter(){
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToNoteWriteFragment(it))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = NotesListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observer()
        loadState()
        newNote()
        checkIfUserDataIsUploaded()
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun observer() {
        viewModel.notes.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun loadState() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.emptyNote.emptyNote.isVisible =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount < 1
                binding.notesLoading.notesLoading.isVisible =
                    loadState.refresh is LoadState.Loading
            }
        }
    }

    private fun newNote() {
        binding.createNote.setOnClickListener {
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToNoteWriteFragment(
                Note()
            ))
        }
    }

    private fun checkIfUserDataIsUploaded() {
        requireContext().getIfUserDataIsUploaded().run {
            if (!this) {
                viewModel.uploadUserData()
                lifecycleScope.launch {
                    requireContext().userDataUploaded()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}