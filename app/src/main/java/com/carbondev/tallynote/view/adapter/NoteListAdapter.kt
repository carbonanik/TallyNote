package com.carbondev.tallynote.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.NoteListItemBinding
import com.carbondev.tallynote.datamodel.Note
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
//        holder.bind( shortTitle( note.content ), note.createdAt , noteListViewModel, note.key)
        holder.bind(note, noteListViewModel)
    }


    private fun shortTitle(title: String): String {
        val trim = title.trim()
        return if (trim.length >= 24) {
            val take20 = trim.take(20).replace("\n", " ")
            val ellipsis = "\u2026"
            "$take20$ellipsis"
        } else {
            trim
        }
    }


    class NoteListViewHolder(val binding: NoteListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(note: Note, viewModel: NoteListViewModel) {
            binding.note = note
            binding.noteListViewModel = viewModel
            binding.executePendingBindings()
        }

//        fun bind(
//            title: String,
//            date: String,
//            noteListViewModel: NoteListViewModel,
//            noteKey: String
//        ) {
//            noteListItemBinding.title = title
//            noteListItemBinding.date = date
//            noteListItemBinding.noteKey = noteKey
//            noteListItemBinding.noteListViewModel = noteListViewModel
//            noteListItemBinding.executePendingBindings()
//        }
    }
}