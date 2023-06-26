package com.carbondev.tallynote.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.StoreProductHistoryItemBinding
import com.carbondev.tallynote.datamodel.StoreProductModified
import com.carbondev.tallynote.view.viewmodel.StoreProductDetailViewModel


class StoreProductHistoryAdapter(private val viewModel: StoreProductDetailViewModel) :
    RecyclerView.Adapter<StoreProductHistoryAdapter.HistorySellViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorySellViewHolder {

        val viewDataBinding = DataBindingUtil.inflate<StoreProductHistoryItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.store_product_history_item,
            parent,
            false
        )

        return HistorySellViewHolder(
            viewDataBinding
        )
    }

    override fun getItemCount(): Int {
        return (viewModel.allStoreProductModified.value ?: arrayListOf()).size
    }

    override fun onBindViewHolder(holder: HistorySellViewHolder, position: Int) {
        val storeProductModified = viewModel.allStoreProductModified.value!![position]
        holder.bind(storeProductModified)
    }

    class HistorySellViewHolder(private val viewDataBinding: StoreProductHistoryItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        fun bind(modified: StoreProductModified) {
            viewDataBinding.stmType.text =
                if (modified.isAdded == true) viewDataBinding.root.context.getString(R.string.buy)
                else viewDataBinding.root.context.getString(R.string.sell)
            viewDataBinding.storeProductModified = modified
            viewDataBinding.executePendingBindings()
        }

    }
}