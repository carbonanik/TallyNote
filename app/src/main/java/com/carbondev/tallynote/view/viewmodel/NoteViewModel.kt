package com.carbondev.tallynote.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.datamodel.Note
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.carbondev.tallynote.utils.DateTimeString

class NoteViewModel : ViewModel(){

    private val remoteDataRepo = FirebaseDataRepository

    val openNote : LiveData<Note>
        get() = remoteDataRepo.openNote

    val noteContent = MutableLiveData<String?>()

    val info = MutableLiveData<String>()
    val onNoteSaveButtonClick = MutableLiveData<Boolean>()

    fun init(noteKey: String){
        if (noteKey.isNotEmpty()){
            remoteDataRepo.fetchSingleNote(noteKey)

        } else {
            remoteDataRepo.fetchEmptyNote()
        }
    }

    fun onNoteSaveButtonClick(){
        onNoteSaveButtonClick.value = true
        if (openNote.value!!.content.isNotEmpty()){

            if (openNote.value!!.key == "" ){
                openNote.value!!.createdAt = DateTimeString().now()
//                openNote.value!!.lastEdited = DateTimeString().now()
                addNote(openNote.value!!)
            } else {
//                openNote.value!!.lastEdited = DateTimeString().now()
                updateNote(openNote.value!!)
            }
        }
    }

    private fun addNote(note: Note){
        remoteDataRepo.saveNote(note)
    }

    private fun updateNote(note: Note){
        remoteDataRepo.updateNote(note)
    }
}


