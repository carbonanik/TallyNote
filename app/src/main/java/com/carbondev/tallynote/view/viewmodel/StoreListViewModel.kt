package com.carbondev.tallynote.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carbondev.tallynote.datamodel.StoreProduct
import com.carbondev.tallynote.datamodel.StoreProductModified
import com.carbondev.tallynote.repository.FirebaseStoreProductRepository
import com.carbondev.tallynote.utils.DateTimeString
import com.carbondev.tallynote.utils.specialAdd
import com.carbondev.tallynote.utils.specialDiv
import com.carbondev.tallynote.utils.specialSub
import com.carbondev.tallynote.view.adapter.StoreProductListAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StoreListViewModel(application: Application) : AndroidViewModel(application) {

    private val remoteDataRepo = FirebaseStoreProductRepository
    private val adapter = StoreProductListAdapter(this, application.applicationContext)

    val liveStoreProductList = remoteDataRepo.allStoreProduct
    val storeProducts = MutableLiveData<ArrayList<StoreProduct>>()

    val onItemClick = MutableLiveData<String>()
    val onItemAddButtonClick = MutableLiveData<String>()
    val onItemRemoveButtonClick = MutableLiveData<String>()

    val onAddStoreProductClick = MutableLiveData<Boolean>()

    fun init() {
        getAllStoreProduct()
    }


    fun storeProductModified(storeProductModified: StoreProductModified, storeProductKey: String) {
        viewModelScope.launch {
            remoteDataRepo.singleStoreProductFlow(storeProductKey).collect { result ->
                storeProductModified.apply {
                    this.storeProductKey = storeProductKey
                }

                when {
                    result.isSuccess -> {
                        result.getOrNull()?.let { storeProduct ->
                            val updatedStoreProduct = storeProduct.copy(
                                countInStock = if (storeProductModified.isAdded == true)
                                    specialAdd(
                                        storeProduct.countInStock,
                                        storeProductModified.count
                                    )
                                else
                                    specialSub(
                                        storeProduct.countInStock,
                                        storeProductModified.count
                                    ),
                                productPrice = if (storeProductModified.isAdded == true)
                                    specialAdd(
                                        storeProduct.productPrice,
                                        storeProductModified.price
                                    )
                                else
                                    specialSub(
                                        storeProduct.productPrice,
                                        storeProductModified.price
                                    ),
                                unitPrice = if (storeProductModified.isAdded == true) specialDiv(
                                    storeProductModified.price,
                                    storeProductModified.count
                                ) else storeProduct.unitPrice
                            )

                            remoteDataRepo.saveStoreProductModified(storeProductModified)
                            remoteDataRepo.updateStoreProduct(updatedStoreProduct)
                        }
                    }
                }

            }
        }
    }


    fun getAdapter(): StoreProductListAdapter {
        return adapter
    }

    fun addNewStoreProduct(storeProduct: StoreProduct) {
        storeProduct.createdAt = DateTimeString().now()
        storeProduct.unitPrice = specialDiv(storeProduct.productPrice, storeProduct.countInStock)


        viewModelScope.launch {
            remoteDataRepo.saveStoreProductFlow(storeProduct).collect { result ->
                when {
                    result.isSuccess -> {
                        result.getOrNull()?.let { storeProduct ->
                            if (storeProduct.countInStock?.isEmpty() == false) {
                                remoteDataRepo.saveStoreProductModified(
                                    StoreProductModified(
                                        isAdded = true,
                                        storeProductKey = storeProduct.key,
                                        count = storeProduct.countInStock,
                                        price = storeProduct.productPrice,
                                        dateTime = DateTimeString().now(),
                                        lastEdited = System.currentTimeMillis(),
                                        note = "Initial items",
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    fun onItemClick(key: String) {
        onItemClick.value = key
    }

    fun onItemAddButtonClick(key: String) {
        onItemAddButtonClick.value = key
    }

    fun onItemRemoveButtonClick(key: String) {
        onItemRemoveButtonClick.value = key
    }

    fun onAddStoreProductClick() {
        onAddStoreProductClick.value = true
    }

    private fun getAllStoreProduct() {
        remoteDataRepo.fetchAllStoreProduct()
    }

    fun refreshList() {
        adapter.notifyDataSetChanged()
    }
}