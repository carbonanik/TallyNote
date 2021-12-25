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
import com.carbondev.tallynote.utils.EdgeDecorator
import com.carbondev.tallynote.view.viewmodel.DetailViewModel

class CustomerDetailPagerAdapter(private val viewModel: DetailViewModel) : PagerAdapter() {

    private lateinit var recyclerView : RecyclerView

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ConstraintLayout
    }

    override fun getCount() = 2

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(container.context)
        var view = View(container.context)

        if (position == 0){
            view = layoutInflater.inflate(R.layout.customer_history_layout, container, false)

            val linearLayoutManager = LinearLayoutManager(container.context)
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true

            CustomerHistoryLayoutBinding.bind(view).apply {

                this.lifecycleOwner = container.context as LifecycleOwner
                this.detailViewModel = viewModel
                this.recyclerViewHistory.layoutManager = linearLayoutManager
                this.recyclerViewHistory.addItemDecoration(EdgeDecorator(200))

                recyclerView = this.recyclerViewHistory
                this.executePendingBindings()
            }


        } else if (position == 1) {
            view = layoutInflater.inflate(R.layout.customer_about_layout, container, false)

            CustomerAboutLayoutBinding.bind(view).apply {
                this.lifecycleOwner = container.context as LifecycleOwner
                this.detailViewModel = viewModel
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
            recyclerView.smoothScrollToPosition(viewModel.sellList.value!!.size)
        }
    }
}
