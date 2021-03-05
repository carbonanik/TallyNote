package com.carbondev.tallynote.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.datamodel.Library
import com.carbondev.tallynote.databinding.LibraryItemBinding
import com.carbondev.tallynote.view.viewmodel.SellCartViewModel

class ProductLibraryAdapter(val viewModel: SellCartViewModel) : RecyclerView.Adapter<ProductLibraryAdapter.ProductLibraryViewHolder>() {

    private val productList = arrayListOf<Library>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductLibraryViewHolder {
        val libraryItemBinding = DataBindingUtil.inflate<LibraryItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.library_item,
            parent,
            false
        )
        return ProductLibraryViewHolder(
            libraryItemBinding
        )
    }

    override fun onBindViewHolder(holder: ProductLibraryViewHolder, position: Int) {
        holder.bind(viewModel, productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun refreshData(pList : ArrayList<Library>){
        productList.clear()
        productList.addAll(pList)
        notifyDataSetChanged()
    }

    class ProductLibraryViewHolder(b: LibraryItemBinding) : RecyclerView.ViewHolder(b.root) {
        private val libraryItemBinding = b
        fun bind(viewModel: SellCartViewModel, library : Library) {
            
            libraryItemBinding.sellCartViewModel = viewModel
            libraryItemBinding.library = library
            libraryItemBinding.executePendingBindings()
        }
    }
}
