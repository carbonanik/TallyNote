package com.carbondev.tallynote.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.NoteListItemBinding
import com.carbondev.tallynote.view.viewmodel.NoteListViewModel

class NoteListAdapter(private val noteListViewModel: NoteListViewModel) : RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        val noteListItemBinding = DataBindingUtil.inflate<NoteListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.note_list_item,
            parent,
            false
        )

        return NoteListViewHolder(
            noteListItemBinding
        )
    }

    override fun getItemCount(): Int {
        return (noteListViewModel.liveNoteList.value ?: arrayListOf()).size
    }

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        val note = noteListViewModel.liveNoteList.value!![position]
        holder.bind( shortTitle( note.content ), note.createdAt , noteListViewModel, note.key)
    }


    private fun shortTitle(title: String): String {
        val t2 = title.take(20)
        return if (title.length > 20){
            "$t2..."
        } else {
            t2
        }
    }


    class NoteListViewHolder(v: NoteListItemBinding) : RecyclerView.ViewHolder(v.root){
        private val noteListItemBinding = v

        fun bind(
            title: String,
            date: String,
            noteListViewModel: NoteListViewModel,
            noteKey: String
        ) {
            noteListItemBinding.title = title
            noteListItemBinding.date = date
            noteListItemBinding.noteKey = noteKey
            noteListItemBinding.noteListViewModel = noteListViewModel
            noteListItemBinding.executePendingBindings()
        }
    }
}