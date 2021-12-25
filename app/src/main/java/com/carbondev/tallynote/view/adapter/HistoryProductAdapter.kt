package com.carbondev.tallynote.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.HistoryItemProductBinding
import com.carbondev.tallynote.datamodel.Product
import com.carbondev.tallynote.datamodel.Sell
import com.carbondev.tallynote.view.viewmodel.DetailViewModel

class HistoryProductAdapter(private val sell: Sell) : RecyclerView.Adapter<HistoryProductAdapter.HistoryProductViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryProductViewHolder {
        val historyItemSellBinding = DataBindingUtil.inflate<HistoryItemProductBinding>(
            LayoutInflater.from(parent.context),
            R.layout.history_item_product,
            parent,
            false
        )

        return HistoryProductViewHolder(
            historyItemSellBinding
        )
    }

    override fun getItemCount(): Int {
        return sell.products.size
    }

    override fun onBindViewHolder(holder: HistoryProductViewHolder, position: Int) {
        holder.bind(sell.products[position], position)
    }

    class HistoryProductViewHolder(v: HistoryItemProductBinding) : RecyclerView.ViewHolder(v.root) {
        private val historyItemProductBinding = v
        fun bind(product: Product, position: Int){
            historyItemProductBinding.product = product
            historyItemProductBinding.serialNumber = (position + 1).toString()
            historyItemProductBinding.executePendingBindings()
        }
    }
}