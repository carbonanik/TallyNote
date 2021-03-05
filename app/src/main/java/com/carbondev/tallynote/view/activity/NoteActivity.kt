package com.carbondev.tallynote.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.carbondev.tallynote.R
import com.carbondev.tallynote.datamodel.INTENT_NOTE
import com.carbondev.tallynote.databinding.ActivityNoteBinding
import com.carbondev.tallynote.view.viewmodel.NoteViewModel

class NoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteViewModel
    private lateinit var binding : ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setBinding(savedInstanceState)

        observer()
    }


    private fun setBinding(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        binding = DataBindingUtil.setContentView<ActivityNoteBinding>(
            this,
            R.layout.activity_note).apply {
            this.lifecycleOwner = this@NoteActivity
            this.noteViewModel = viewModel
//            setSupportActionBar(this.notePadToolbar)
        }
        if (savedInstanceState == null){
            viewModel.init(getNoteKeyIntent())
        }

        listener()
//        binding.editText4.setSelection(10)
    }

    private fun getNoteKeyIntent(): String {
        return intent.getStringExtra(INTENT_NOTE) ?: ""

    }

    private fun observer(){
        viewModel.openNote.observe( this , {

            if (viewModel.onNoteSaveButtonClick.value == null){
                viewModel.noteContent.value = viewModel.openNote.value!!.content
//                binding.editText4.text.clear()
//                binding.editText4.setSelection(1)
            }
        })

        viewModel.onNoteSaveButtonClick.observe( this , {
            binding.saveButton.visibility = View.INVISIBLE
        })

        viewModel.noteContent.observe(this, {
            viewModel.openNote.value?.content = viewModel.noteContent.value!!
            binding.saveButton.visibility = View.VISIBLE

        })
    }

    private fun listener(){
        binding.backButton.setOnClickListener {
            super.onBackPressed()
        }
    }
}
