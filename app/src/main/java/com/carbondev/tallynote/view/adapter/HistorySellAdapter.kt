package com.carbondev.tallynote.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.HistoryItemSellBinding
import com.carbondev.tallynote.databinding.HistoryItemSellPaymentBinding
import com.carbondev.tallynote.datamodel.SELL_PAYMENT
import com.carbondev.tallynote.datamodel.SELL_PRODUCT
import com.carbondev.tallynote.datamodel.Sell
import com.carbondev.tallynote.datamodel.typeProduct
import com.carbondev.tallynote.utils.EdgeDecorator
import com.carbondev.tallynote.view.viewmodel.DetailViewModel


class HistorySellAdapter(private val viewModel: DetailViewModel) : RecyclerView.Adapter<HistorySellAdapter.HistorySellViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorySellViewHolder {

         val viewDataBinding = when (viewType) {
             SELL_PRODUCT -> {
                 DataBindingUtil.inflate<HistoryItemSellBinding>(
                     LayoutInflater.from(parent.context),
                     R.layout.history_item_sell,
                     parent,
                     false
                 )
             }

             else -> {
                 DataBindingUtil.inflate<HistoryItemSellPaymentBinding>(
                     LayoutInflater.from(parent.context),
                     R.layout.history_item_sell_payment,
                     parent,
                     false
                 )
             }
         }



        return HistorySellViewHolder(
            viewDataBinding
        )
    }

    override fun getItemCount(): Int {
        return (viewModel.sellList.value ?: arrayListOf()) .size
    }

    override fun onBindViewHolder(holder: HistorySellViewHolder, position: Int) {

        val sellForThisPosition = viewModel.sellList.value!![position]

        if (sellForThisPosition.type == typeProduct){
            holder.bindSellProduct(sellForThisPosition)
        } else {
            holder.bindSellPayment(sellForThisPosition)
        }

    }

    override fun getItemViewType(position: Int): Int {

        return if (viewModel.sellList.value!![position].type == typeProduct){
            SELL_PRODUCT
        } else {
            SELL_PAYMENT
        }

    }

    class HistorySellViewHolder(private val viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewDataBinding.root){

        fun bindSellProduct(sell: Sell){
            viewDataBinding as HistoryItemSellBinding
            viewDataBinding.sell = sell
            println("Product")
            println(sell)
            viewDataBinding.historyProductListRecyclerView.adapter = HistoryProductAdapter(sell)
            viewDataBinding.historyProductListRecyclerView.addItemDecoration(EdgeDecorator(5, EdgeDecorator.ALL))

            viewDataBinding.executePendingBindings()
        }

        fun bindSellPayment(sell: Sell){
            viewDataBinding as HistoryItemSellPaymentBinding
            viewDataBinding.sell = sell
            println("Payment")
            println(sell)
            viewDataBinding.executePendingBindings()
        }
    }
}