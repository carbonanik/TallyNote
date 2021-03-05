package com.carbondev.tallynote.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EdgeDecorator(private val edgePadding : Int, private val type : Int = REVERSED ) : RecyclerView.ItemDecoration() {

    companion object{
        const val NORMAL = 0
        const val REVERSED = 1
        const val ALL = 2
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {

        super.getItemOffsets(outRect, view, parent, state)

        val itemCount = state.itemCount
        val itemPosition = parent.getChildAdapterPosition(view)

        // no position , leave it alone
        if (itemPosition == RecyclerView.NO_POSITION){
            return
        }

        // last position
        if(itemCount > 0 && itemPosition == itemCount - 1 && type == NORMAL) {
            outRect.set(view.paddingLeft, view.paddingTop, view.paddingRight, edgePadding)
        }

        else if (itemCount > 0 && itemPosition == 0 && type == REVERSED){
            outRect.set(view.paddingLeft, view.paddingTop, view.paddingRight, edgePadding)
        }

        else if(type == ALL)
            outRect.set(0, edgePadding, 0, 0)
        // every other position
        else{
            outRect.set(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
        }
    }

}