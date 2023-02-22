package com.faithdeveloper.noted.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.faithdeveloper.noted.R
import com.faithdeveloper.noted.databinding.NoteListItemBinding
import com.faithdeveloper.noted.models.Note
import com.faithdeveloper.noted.ui.utils.NotesDiffUtil
import com.faithdeveloper.noted.ui.utils.Util

class NotePagingAdapter(
    val click: (note: Note) -> Unit,
    val longClick: (id: String, count: Int, add: Boolean, position: Int) -> Unit
) :
    PagingDataAdapter<Note, NotePagingAdapter.NoteViewHolder>(
        NotesDiffUtil
    ) {
    private var longClickMap = mutableMapOf<Int, Boolean>()
    private var longClickCount = 0
    private var unClickAllFlag = false

    inner class NoteViewHolder(private val binding: NoteListItemBinding) :
        ViewHolder(binding.root) {
        private val item: ConstraintLayout = binding.noteListItem
        private lateinit var note: Note

        init {
            item.setOnClickListener {
                if (longClickCount == 0) {
                    click.invoke(note)
                } else if (longClickCount == 1) {
                    if (longClickMap[layoutPosition] == true) {
                        unLongClick()
                    } else {
                        longClickView()
                    }
                } else {
                    if (longClickMap[layoutPosition] == true) {
                        unLongClick()
                    } else {
                        longClickView()
                    }
                }
            }
            item.setOnLongClickListener {
                if (longClickCount == 0) {
                    longClickView()
                } else if (longClickCount == 1) {
                    if (longClickMap[layoutPosition] == true) {
                        unLongClick()
                    } else {
                        longClickView()
                    }
                } else {
                    if (longClickMap[layoutPosition] == true) {
                        unLongClick()
                    } else {
                        longClickView()
                    }
                }

                return@setOnLongClickListener true
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun longClickView() {
            unClickAllFlag = false
            longClickMap[layoutPosition] = true
            longClickCount++
            binding.noteListItem.background =
                itemView.resources.getDrawable(R.drawable.search_background_selected)
            binding.date.setTextColor(itemView.resources.getColor(R.color.white))
            binding.noteExtract.setTextColor(itemView.resources.getColor(R.color.white))
            binding.title.setTextColor(itemView.resources.getColor(R.color.white))
            longClick.invoke(note.trackingId, longClickCount, true, layoutPosition)
        }


        @SuppressLint("UseCompatLoadingForDrawables")
        private fun unLongClick() {
            longClickMap[layoutPosition] = false
            longClickCount--
            returnViewsBackground()
            longClick.invoke(note.trackingId, longClickCount, false, layoutPosition)
        }

        private fun returnViewsBackground() {
            binding.noteListItem.background =
                itemView.resources.getDrawable(R.drawable.search_background)
            binding.date.setTextColor(itemView.resources.getColor(R.color.date_text_color))
            binding.noteExtract.setTextColor(itemView.resources.getColor(R.color.extract_text_color))
            binding.title.setTextColor(itemView.resources.getColor(R.color.title_text_color))
        }

        fun bind(note: Note) {
            if (unClickAllFlag) {
                returnViewsBackground()
            }
            this.note = note
            binding.noteExtract.text = note.note
            binding.title.text = note.title
            binding.date.text = Util.formatDate(note.lastUpdated)
            longClickMap[layoutPosition].run {
                if (this == null) longClickMap[layoutPosition] = false
                else {
//                  do nothing
                }
            }
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

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    fun unClickAll() {
        unClickAllFlag = true
        longClickMap.onEach {
            longClickMap[it.key] = false
        }
        longClickCount = 0
        notifyDataSetChanged()
    }
}