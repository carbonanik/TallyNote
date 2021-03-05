package com.carbondev.tallynote.view.adapter

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
import com.carbondev.tallynote.utils.MyDateFormat
import com.carbondev.tallynote.view.viewmodel.DetailViewModel


class HistorySellAdapter(private val viewModel: DetailViewModel) : RecyclerView.Adapter<HistorySellAdapter.HistorySellViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

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

        if (viewModel.sellList.value!![position].type == typeProduct){
//            viewPool.setMaxRecycledViews(0,1)
            holder.bindSellProduct(viewModel, position, viewPool)
        } else {
            holder.bindSellPayment(viewModel, position)
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

        fun bindSellProduct(
            viewModel: DetailViewModel,
            position: Int,
            viewPool: RecyclerView.RecycledViewPool
        ){

            viewDataBinding as HistoryItemSellBinding
            viewDataBinding.detailViewModel = viewModel
            viewDataBinding.sell = viewModel.sellList.value!![position]
            viewDataBinding.beforeDueOrAdv = calculateBeforeDueOrAdv(viewModel.sellList.value!![position].beforeDue)
            viewDataBinding.afterDueOrAdv = calculateAfterDueOrAdv(viewModel.sellList.value!![position].afterDue)
            viewDataBinding.position = position
            viewDataBinding.date = dateString(viewModel.sellList.value!![position])
            viewDataBinding.historyProductListRecyclerView.layoutManager = LinearLayoutManager(viewModel.detailActivityContext)
            viewDataBinding.historyProductListRecyclerView.adapter = HistoryProductAdapter(viewModel, position)
            viewDataBinding.historyProductListRecyclerView.addItemDecoration(EdgeDecorator(1, EdgeDecorator.ALL))
//            viewDataBinding.historyProductListRecyclerView.setRecycledViewPool(viewPool)

            viewDataBinding.executePendingBindings()
        }

        fun bindSellPayment(viewModel: DetailViewModel, position: Int){
            viewDataBinding as HistoryItemSellPaymentBinding
            viewDataBinding.detailViewModel = viewModel
            viewDataBinding.pay = viewModel.sellList.value!![position].payment
            viewDataBinding.due = calculateAfterDueOrAdv(viewModel.sellList.value!![position].afterDue)
            viewDataBinding.detail = viewModel.sellList.value!![position].note
            viewDataBinding.date = dateString(viewModel.sellList.value!![position])
            viewDataBinding.executePendingBindings()
        }

        private fun calculateBeforeDueOrAdv(beforeTotalDue: String): String? {

            return if (beforeTotalDue[0] == '-'){
                viewDataBinding.root.context.getString(R.string.adv_before) +  beforeTotalDue.substring(1)
            } else{
                viewDataBinding.root.context.getString(R.string.total_due_before) + beforeTotalDue
            }
        }

        private fun calculateAfterDueOrAdv(afterTotalDue : String): String {
            return if (afterTotalDue[0] == '-'){
                viewDataBinding.root.context.getString(R.string.adv_after) +  afterTotalDue.substring(1)
            } else{
                viewDataBinding.root.context.getString(R.string.total_due_after) + afterTotalDue
            }
        }

        private fun dateString(sell: Sell): String {
            val myDateFormat = MyDateFormat(sell.date)
            return myDateFormat.sellDateString()
        }
    }
}