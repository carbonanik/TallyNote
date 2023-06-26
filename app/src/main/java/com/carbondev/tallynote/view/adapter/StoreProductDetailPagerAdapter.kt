package com.carbondev.tallynote.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.CustomerAboutLayoutBinding
import com.carbondev.tallynote.databinding.CustomerHistoryLayoutBinding
import com.carbondev.tallynote.databinding.StoreProductAboutLayoutBinding
import com.carbondev.tallynote.databinding.StoreProductHistoryLayoutBinding
import com.carbondev.tallynote.datamodel.ABOUT_TAB
import com.carbondev.tallynote.datamodel.HISTORY_TAB
import com.carbondev.tallynote.utils.EdgeDecorator
import com.carbondev.tallynote.view.viewmodel.DetailViewModel
import com.carbondev.tallynote.view.viewmodel.StoreProductDetailViewModel

class StoreProductDetailPagerAdapter(private val viewModel: StoreProductDetailViewModel) : PagerAdapter() {

    private lateinit var recyclerView : RecyclerView

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ConstraintLayout
    }

    override fun getCount() = 2

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(container.context)
        var view = View(container.context)

        if (position == HISTORY_TAB){
            view = layoutInflater.inflate(R.layout.store_product_history_layout, container, false)

            val linearLayoutManager = LinearLayoutManager(container.context)
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true

            StoreProductHistoryLayoutBinding.bind(view).apply {

                this.lifecycleOwner = container.context as LifecycleOwner
                this.viewModel = this@StoreProductDetailPagerAdapter.viewModel
                this.recyclerViewHistory.layoutManager = linearLayoutManager
                this.recyclerViewHistory.addItemDecoration(EdgeDecorator(200))

                recyclerView = this.recyclerViewHistory
                this.executePendingBindings()
            }


        } else if (position ==  ABOUT_TAB) {
            view = layoutInflater.inflate(R.layout.store_product_about_layout, container, false)

            StoreProductAboutLayoutBinding.bind(view).apply {
                this.lifecycleOwner = container.context as LifecycleOwner
                this.viewModel = this@StoreProductDetailPagerAdapter.viewModel
                this.executePendingBindings()
            }

        }

        container.addView(view)
        return view
    }

    fun goSearchPosition(position: Int){
        recyclerView.smoothScrollToPosition(position)
    }

    fun scrollToTop(){
        if (::recyclerView.isInitialized){
            recyclerView.smoothScrollToPosition(viewModel.allStoreProductModified.value!!.size)
        }
    }
}
