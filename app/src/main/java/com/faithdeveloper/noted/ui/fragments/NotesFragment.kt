package com.faithdeveloper.noted.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.faithdeveloper.noted.R
import com.faithdeveloper.noted.data.NotedApplication
import com.faithdeveloper.noted.databinding.NotesListBinding
import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.ui.adapters.NotePagingAdapter
import com.faithdeveloper.noted.ui.utils.Util
import com.faithdeveloper.noted.ui.utils.Util.getIfUserDataIsUploaded
import com.faithdeveloper.noted.ui.utils.Util.userDataUploaded
import com.faithdeveloper.noted.viewmodels.NotesListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class NotesFragment : Fragment() {
    private var deleteFlag by Delegates.notNull<Boolean>()
    private var _binding: NotesListBinding? = null
    private lateinit var adapter: NotePagingAdapter
    private val viewModel: NotesListViewModel by viewModels { NotesListViewModel.Factory }
    private lateinit var application: NotedApplication
    private lateinit var idsOfNotesToDelete: MutableList<String>
    private lateinit var positionsOfNotesToDelete: MutableList<Int>
    private var appPausedFlag: Boolean
    private var longClickFlag: Boolean
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var dialog: AlertDialog? = null
    private var SORT_TYPE = 0
    init {
        appPausedFlag = false
        longClickFlag = false
        deleteFlag = false
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = requireActivity().application as NotedApplication
        idsOfNotesToDelete = mutableListOf()
        positionsOfNotesToDelete = mutableListOf()
        adapter = NotePagingAdapter(
            click = {
                findNavController().navigate(
                    NotesFragmentDirections.actionNotesFragmentToNoteWriteFragment(
                        it
                    )
                )
            },
            longClick = { id, count, add, position ->
                if (add) {
                    idsOfNotesToDelete.add(id)
                    positionsOfNotesToDelete.add(position)
                } else {
                    positionsOfNotesToDelete.remove(position)
                    idsOfNotesToDelete.remove(id)
                }
                when (count) {
                    0 -> loadNormalToolbar()
                    1 -> loadLongClickToolbar()
                    else -> binding.actionToolbar.count.text = count.toString()
                }
            }
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = NotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (longClickFlag) {
                    clicksCancelAction()
                } else handleOnBackPressed()
            }
        setUpRecyclerView()
        observer()
        loadState()
        newNote()
        checkIfUserDataIsUploaded()
    }

    private fun loadNormalToolbar() {
        longClickFlag = false
        binding.actionToolbar.actionToolbar.isVisible = false
        binding.notesListToolbar.notesListToolbar.isVisible = true
        binding.searchToolbar.searchToolbar.isVisible = false
        binding.actionToolbar.count.text = 0.toString()
        sort()
    }


    private fun loadLongClickToolbar() {
        longClickFlag = true
        binding.notesListToolbar.notesListToolbar.isVisible = false
        binding.actionToolbar.actionToolbar.isVisible = true
        binding.searchToolbar.searchToolbar.isVisible = false
        binding.actionToolbar.count.text = 1.toString()
        delete()
        cancelClicks()
    }

    private fun delete() {
        binding.actionToolbar.delete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Note(s)")
                .setMessage("Do you want to discard this note(s)? You won't be able to retrieve again")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    application.deleteNote(idsOfNotesToDelete)
                    clicksCancelAction()
                    appPausedFlag = true
                    adapter.refresh()
                    showDialog()?.show()
                    idsOfNotesToDelete = mutableListOf()
                    positionsOfNotesToDelete = mutableListOf()
                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
//                    do nothing
                }).create().show()
        }
    }

    private fun sort() {
        binding.notesListToolbar.sort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun showSortDialog() {
        val sortTypes = resources.getStringArray(R.array.sort_types)
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.sort))
            .setSingleChoiceItems(
                resources.getStringArray(R.array.sort_types),
                SORT_TYPE
            ) { dialog, which ->
                when (sortTypes[which]) {
                    "Latest" -> reload(Util.SORT_TYPES.LATEST)
                    "Oldest" -> reload(Util.SORT_TYPES.OLDEST)
                }
                dialog?.dismiss()
            }
        dialog = dialogBuilder.create()
        dialog?.show()
    }

    private fun reload(sort_type: Util.SORT_TYPES) {
        viewModel.setSortType(sort_type)
        adapter.refresh()
    }

    private fun showDialog(): AlertDialog? {
        deleteFlag = true
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage("Deleting..")
            .setCancelable(false)
            .create()
        dialog?.setOnDismissListener {
            deleteFlag = false
        }
        return dialog

    }

    private fun cancelClicks() {
        binding.actionToolbar.cancel.setOnClickListener {
            clicksCancelAction()
        }
    }

    private fun clicksCancelAction() {
        adapter.unClickAll()
        loadNormalToolbar()
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
                    loadState.refresh is LoadState.Loading && appPausedFlag == false
                binding.notesListToolbar.sort.isVisible = adapter.itemCount > 1
                if (deleteFlag && loadState.refresh is LoadState.NotLoading) dialog?.dismiss()
            }
        }
    }

    private fun newNote() {
        binding.createNote.setOnClickListener {
            adapter.unClickAll()
            idsOfNotesToDelete = mutableListOf()
            positionsOfNotesToDelete = mutableListOf()
            findNavController().navigate(
                NotesFragmentDirections.actionNotesFragmentToNoteWriteFragment(
                    Note()
                )
            )
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

    override fun onPause() {
        super.onPause()
        appPausedFlag = true
    }

    override fun onResume() {
        super.onResume()
        if (appPausedFlag) {
            adapter.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}