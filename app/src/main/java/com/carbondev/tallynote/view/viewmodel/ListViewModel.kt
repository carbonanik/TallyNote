package com.carbondev.tallynote.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.carbondev.tallynote.utils.DateTimeString
import com.carbondev.tallynote.view.adapter.CustomerListAdapter
import java.util.*

class ListViewModel(application: Application): AndroidViewModel(application) {

    private val remoteDataRepo   = FirebaseDataRepository
    private var adapter = CustomerListAdapter(this, application.applicationContext)

    val customerList = MutableLiveData<ArrayList<Customer>>()
    val liveCustomerList = remoteDataRepo.allCustomer


    val onItemClick = MutableLiveData<String>()
    val onCartClick = MutableLiveData<Boolean>()
    val onOptionCartClick = MutableLiveData<String>()
    val onAddCustomerClick = MutableLiveData<Boolean>()
    val onNoteClick = MutableLiveData<Boolean>()
    val onSettingClick = MutableLiveData<Boolean>()
    val onOpenStoreClick = MutableLiveData<Boolean>()

    val ownerName = remoteDataRepo.ownerName

    var ownerTotalDue = MutableLiveData<String>()


    fun init(){
        getAllCustomer()
        getOwnerName()
    }


    fun getAdapter(): CustomerListAdapter {
        return adapter
    }

    fun refreshList() {
//        adapter?.setData(liveCustomerList.value ?: arrayListOf())
        adapter.notifyDataSetChanged()
        calculateTotalDue()
    }

    fun onItemClick(customerKey: String) {
        onItemClick.value = customerKey

    }

    fun onSettingClick(){
        onSettingClick.value = true
    }

    fun onOptionCartClick(customerKey: String) {
        onOptionCartClick.value = customerKey
    }

    fun onAddCustomerButtonClick() {
        onAddCustomerClick.value = true
    }

    fun onNoteClick() {
        onNoteClick.value = true
    }

    fun onOpenStoreClick() {
        onOpenStoreClick.value = true
    }

    fun addNewCustomer(customer: Customer){
        customer.createdAt = DateTimeString().now()
        remoteDataRepo.saveCustomer(customer)
    }

    private fun getOwnerName(){
        remoteDataRepo.fetchOwnerName()
    }

    fun queryFilter(qt: String): MutableList<Customer> {
        val queryText = qt.toLowerCase(Locale.getDefault())
        val searchCustomerList : MutableList<Customer> = mutableListOf()

        if (queryText.isBlank()) {
            searchCustomerList.addAll(liveCustomerList.value ?: arrayListOf())
        } else {
            for (c in liveCustomerList.value ?: arrayListOf()) {
                if (c.name.toLowerCase(Locale.getDefault()).startsWith(queryText)){ // previously .contains()
                    searchCustomerList.add(c)
                }
            }
        }
        return searchCustomerList
    }

    private fun getAllCustomer(){
        remoteDataRepo.fetchAllCustomer()
    }

    private fun calculateTotalDue(){
        var t = 0.0
        liveCustomerList.value?.forEach {
            t += it.totalDue.toDouble()
        }
        ownerTotalDue.value = String.format("%.0f", t)
    }
}