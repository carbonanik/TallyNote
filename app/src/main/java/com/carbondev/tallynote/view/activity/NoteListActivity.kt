package com.carbondev.tallynote.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.ActivityNoteListBinding
import com.carbondev.tallynote.databinding.NoteDeleteDialogBinding
import com.carbondev.tallynote.datamodel.INTENT_NOTE
import com.carbondev.tallynote.view.viewmodel.NoteListViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class NoteListActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteListViewModel
    private lateinit var binding : ActivityNoteListBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        setBinding()
        observers()
    }

    override fun onRestart(){
        viewModel.refreshNoteList()
        super.onRestart()
    }

    private fun setBinding() {
        val linearLayoutManager = LinearLayoutManager(this)

        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        viewModel = ViewModelProvider(this).get(NoteListViewModel::class.java)

        binding = DataBindingUtil.setContentView<ActivityNoteListBinding>(
            this,
            R.layout.activity_note_list
        ).apply{
            this.lifecycleOwner = this@NoteListActivity
            this.noteListViewModel = viewModel
            this.noteListRecyclerView.layoutManager = linearLayoutManager
        }
        viewModel.init()

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.noteListRecyclerView)

        listener()
    }

    private fun observers(){
        viewModel.noteAddButtonClick.observe( this , {
            val intent = Intent(this, NoteActivity::class.java)
            this.startActivity(intent)
        })

        viewModel.noteItemClick.observe( this , { noteKey->
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra(INTENT_NOTE, noteKey )
            this.startActivity(intent)
        })

        viewModel.liveNoteList.observe( this, {
            viewModel.refreshNoteList()
            goTop()
        })

    }

    private fun goTop(){
        binding.noteListRecyclerView.smoothScrollToPosition(viewModel.liveNoteList.value!!.size)
    }

    private fun listener(){
        binding.backButton.setOnClickListener {
            super.onBackPressed()
        }
    }

    private val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val deleteDialog = AlertDialog.Builder(this@NoteListActivity).create()
            val view = layoutInflater.inflate(R.layout.note_delete_dialog, null)
            val binding = NoteDeleteDialogBinding.bind(view)
            deleteDialog.setView(view)
            binding.confirmButton.setOnClickListener {
                viewModel.deleteNote(position)
                deleteDialog.dismiss()
            }

            binding.cancelButton.setOnClickListener {
                viewModel.refreshItemPosition(viewHolder.adapterPosition)
                deleteDialog.dismiss()
            }
            deleteDialog.setOnCancelListener {
                viewModel.refreshItemPosition(viewHolder.adapterPosition)
            }
            deleteDialog.show()
//            viewModel.deleteSell(position)
//            list.removeAt(position)
//            recyclerAdapter.notifyItemRemoved(position)
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                                 viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                                 actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                .addSwipeRightBackgroundColor(ContextCompat.getColor(this@NoteListActivity, R.color.darkText))
                .addSwipeRightActionIcon(R.drawable.ic_delete_black_24dp)
                .create()
                .decorate()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}
