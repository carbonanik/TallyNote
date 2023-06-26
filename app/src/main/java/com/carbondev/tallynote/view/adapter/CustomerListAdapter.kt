package com.carbondev.tallynote.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.CustomerItemLayoutBinding
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.view.viewmodel.ListViewModel


class CustomerListAdapter(private val listViewModel: ListViewModel, private val context: Context) : RecyclerView.Adapter<CustomerListAdapter.CustomerListViewHolder>() {

//    private var customersList : MutableList<Customer> = mutableListOf()

    private var previousBindingPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerListViewHolder {
        val customerItemLayoutBinding = DataBindingUtil.inflate<CustomerItemLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.customer_item_layout,
            parent,
            false
        )
        return CustomerListViewHolder(
            customerItemLayoutBinding
        )
    }

    override fun getItemCount(): Int {
        return (listViewModel.customerList.value ?: arrayListOf()).size
    }

    override fun onBindViewHolder(holder: CustomerListViewHolder, position: Int) {

        val customer = listViewModel.customerList.value!![position]
        holder. bind(customer, listViewModel)

        animateItemView(holder, position)
    }

    private fun animateItemView(holder: CustomerListViewHolder, position: Int) {

        if (previousBindingPosition < position){
            holder.itemView.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition_reverse_animation)
            previousBindingPosition = position
        } else {
            holder.itemView.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation)
            previousBindingPosition = position
        }
    }

//    fun setData (cL : MutableList<Customer>){
//        customersList = cL
//        notifyDataSetChanged()
//    }


    class CustomerListViewHolder(v : CustomerItemLayoutBinding) : RecyclerView.ViewHolder(v.root) {
        private val customerItemLayoutBinding = v

        fun bind(customer: Customer, viewModel: ListViewModel) {

            customerItemLayoutBinding.itemCustomer = customer
            customerItemLayoutBinding.itemCustomerViewModel = viewModel
            customerItemLayoutBinding.executePendingBindings()
        }
    }
}
