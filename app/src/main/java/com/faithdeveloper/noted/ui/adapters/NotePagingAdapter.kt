package com.faithdeveloper.noted.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.faithdeveloper.noted.databinding.NoteListItemBinding
import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.ui.utils.NotesDiffUtil
import com.faithdeveloper.noted.ui.utils.Util

class NotePagingAdapter(val click: (note: Note) -> Unit) :
    PagingDataAdapter<Note, NotePagingAdapter.NoteViewHolder>(
        NotesDiffUtil
    ) {

    inner class NoteViewHolder(private val binding: NoteListItemBinding) :
        ViewHolder(binding.root) {
        private val item: ConstraintLayout = binding.noteListItem
        private lateinit var note: Note

        init {
            item.setOnClickListener {
                click.invoke(note)
            }
        }

        fun bind(note: Note) {
            this.note = note
            binding.noteExtract.text = note.note
            binding.title.text = note.title
            binding.date.text = Util.formatDate(note.lastUpdated)
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            NoteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }
}