package com.carbondev.tallynote.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.HistoryItemProductBinding
import com.carbondev.tallynote.datamodel.Product

class SellItemAdapter : RecyclerView.Adapter<SellItemAdapter.SellItemViewHolder>() {

    var productList : MutableList<Product> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellItemViewHolder {
        val historyItemProductBinding = DataBindingUtil.inflate<HistoryItemProductBinding>(
            LayoutInflater.from(parent.context),
            R.layout.history_item_product,
            parent,
            false
        )
        return SellItemViewHolder(
            historyItemProductBinding
        )
    }

    override fun getItemCount(): Int {
//        println("item count ${productList.size}")
        return productList.size

    }

    override fun onBindViewHolder(holder: SellItemViewHolder, position: Int) {
        holder.bind(productList[position], position)

//        println(position)
//        println(productList[position])
    }

    fun setData(pl : MutableList<Product>){
        productList.clear()
        productList.addAll(pl)
        notifyDataSetChanged()
    }

    class SellItemViewHolder(b: HistoryItemProductBinding) : RecyclerView.ViewHolder(b.root){

        private val historyItemProductBinding = b
        fun bind(product : Product, sNo: Int) {
//            println("bind")
            historyItemProductBinding.product = product
            historyItemProductBinding.serialNumber = ( sNo + 1 ).toString()
            historyItemProductBinding.executePendingBindings()
        }
    }

}
