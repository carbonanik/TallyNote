package com.carbondev.tallynote.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.datamodel.Note
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.carbondev.tallynote.view.adapter.NoteListAdapter

class NoteListViewModel : ViewModel() {

    private val remoteDataRepo = FirebaseDataRepository
    private var noteListAdapter : NoteListAdapter? = null

    val liveNoteList : LiveData<ArrayList<Note>>
        get() = remoteDataRepo.allNote

    val info = MutableLiveData<String>()

    var noteAddButtonClick = MutableLiveData<Boolean>()
    var noteItemClick = MutableLiveData<String>()


    fun init(){
        noteListAdapter = NoteListAdapter(this)
        getAllNote()
    }

    private fun getAllNote(){
        remoteDataRepo.fetchAllNote()
    }

    fun refreshNoteList(){
        noteListAdapter!!.notifyDataSetChanged()
    }

    fun getNoteListAdapter(): NoteListAdapter? {
        return noteListAdapter
    }

    fun onNoteAddButtonClick(){
        noteAddButtonClick.value = true
    }

    fun onNoteItemClick(noteKey : String){
        noteItemClick.value = noteKey
    }

    fun refreshItemPosition(position : Int){
        noteListAdapter!!.notifyItemChanged(position)
    }

    fun deleteNote(position: Int){
        val noteKey = liveNoteList.value!![position].key
        remoteDataRepo.deleteNote(noteKey)
    }
}