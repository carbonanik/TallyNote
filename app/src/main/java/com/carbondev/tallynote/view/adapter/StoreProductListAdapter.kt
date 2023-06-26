package com.carbondev.tallynote.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.StoreItemLayoutBinding
import com.carbondev.tallynote.datamodel.StoreProduct
import com.carbondev.tallynote.view.viewmodel.StoreListViewModel

class StoreProductListAdapter(private val viewModel: StoreListViewModel, private val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<StoreProductListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<StoreItemLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.store_item_layout,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        println( viewModel.storeProducts.value?.size)
        return viewModel.storeProducts.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storeProduct = viewModel.storeProducts.value!![position]
        holder.bind(storeProduct, viewModel)
    }

    class ViewHolder(val binding: StoreItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind( storeProduct: StoreProduct, viewModel: StoreListViewModel) {
            println("storeProducts")
            println(storeProduct)
            binding.itemStoreProduct = storeProduct
            binding.itemCustomerViewModel = viewModel
            binding.executePendingBindings()
        }
    }
}