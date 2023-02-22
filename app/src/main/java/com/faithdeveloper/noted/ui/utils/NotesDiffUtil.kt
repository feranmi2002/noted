package com.faithdeveloper.noted.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.faithdeveloper.noted.models.Note

object NotesDiffUtil: DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
}