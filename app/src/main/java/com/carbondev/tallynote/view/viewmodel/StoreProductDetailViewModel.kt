package com.carbondev.tallynote.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.carbondev.tallynote.datamodel.StoreProduct
import com.carbondev.tallynote.datamodel.StoreProductModified
import com.carbondev.tallynote.repository.FirebaseStoreProductRepository
import com.carbondev.tallynote.utils.specialDiv
import com.carbondev.tallynote.view.adapter.StoreProductDetailPagerAdapter
import com.carbondev.tallynote.view.adapter.StoreProductHistoryAdapter

class StoreProductDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val remoteDataRepo = FirebaseStoreProductRepository

    private var historyAdapter: StoreProductHistoryAdapter? = null
    private var pagerAdapter: StoreProductDetailPagerAdapter? = null

    val currentStoreProduct: LiveData<StoreProduct>
        get() = remoteDataRepo.singleStoreProduct

    val allStoreProductModified: LiveData<ArrayList<StoreProductModified>>
        get() = remoteDataRepo.allStoreProductModified

//    private val allDateMillis: MutableList<Long> = arrayListOf()
//    private val pickedDate = MutableLiveData<String>()

    val onEditStoreProductClick = MutableLiveData<Boolean>()
    val onDeleteStoreProductClick = MutableLiveData<Boolean>()

    val onBuyClick = MutableLiveData<Boolean>()
    val onSellClick = MutableLiveData<Boolean>()

    val onDatePickerClick = MutableLiveData<Boolean>()


    fun init(storeProductKey: String) {
        declareAdapters()

        emptyStoreProduct()
        emptyStoreProductModifedList()

        getSingleStoreProduct(storeProductKey)
        getAllModifiedForThisStoreProduct(storeProductKey)
    }

    private fun getSingleStoreProduct(key: String) {
        remoteDataRepo.fetchSingleStoreProduct(key)
    }

    private fun getAllModifiedForThisStoreProduct(key: String) {
        remoteDataRepo.getAllModifiedForThisStoreProduct(key)
    }

    fun notifyHistoryAdapter() {
        historyAdapter?.notifyDataSetChanged()
    }


    fun getHistoryAdapter(): StoreProductHistoryAdapter? {
        return historyAdapter
    }

    fun getPagerAdapter(): StoreProductDetailPagerAdapter? {
        return pagerAdapter
    }

    fun onEditStoreProductClick() {
        onEditStoreProductClick.value = true
    }

    fun onBuyButtonClick() {
        onBuyClick.value = true
    }

    fun onSellButtonClick() {
        onSellClick.value = true
    }

    fun onDatePickerClick() {
        onDatePickerClick.value = true
    }

    fun onDeleteStoreProductClick() {
        onDeleteStoreProductClick.value = true
    }

    private fun specialAdd(a: String?, b: String?): String {
        return ((a?.toFloatOrNull() ?: 0f) + (b?.toFloatOrNull() ?: 0f)).toString()
    }

    private fun specialSub(a: String?, b: String?): String {
        return ((a?.toFloatOrNull() ?: 0f).minus(b?.toFloatOrNull() ?: 0f)).toString()
    }

    fun storeProductModified(storeProductModified: StoreProductModified) {
        val storeProduct = currentStoreProduct.value?.copy(
            countInStock = if (storeProductModified.isAdded == true)
                specialAdd(currentStoreProduct.value?.countInStock, storeProductModified.count)
            else
                specialSub(currentStoreProduct.value?.countInStock, storeProductModified.count),
            productPrice = if (storeProductModified.isAdded == true)
                specialAdd(currentStoreProduct.value?.productPrice, storeProductModified.price)
            else
                specialSub(currentStoreProduct.value?.productPrice, storeProductModified.price),
            unitPrice = if (storeProductModified.isAdded == true) specialDiv(
                storeProductModified.price,
                storeProductModified.count
            ) else currentStoreProduct.value?.unitPrice
        )

        val spMod = currentStoreProduct.value?.key?.let {
            storeProductModified.copy(
                storeProductKey = it
            )
        }

        spMod?.let { remoteDataRepo.saveStoreProductModified(it) }
        storeProduct?.let { remoteDataRepo.updateStoreProduct(it) }
    }

    fun submitUpdatedStoreProduct(storeProduct: StoreProduct) {
        remoteDataRepo.updateStoreProduct(storeProduct)
    }


    private fun declareAdapters() {
        pagerAdapter = StoreProductDetailPagerAdapter(this)
        historyAdapter = StoreProductHistoryAdapter(this)
    }

    private fun emptyStoreProduct() {
        remoteDataRepo.fetchEmptySingleStoreProduct()
    }

    private fun emptyStoreProductModifedList() {
        remoteDataRepo.fetchEmptyStoreProductModifedList()
    }

    fun deleteStoreProduct() {
        if (!currentStoreProduct.value?.key.isNullOrEmpty()) {
            remoteDataRepo.deleteStoreProduct(currentStoreProduct.value!!.key)
        }
    }

    fun scrollToTop() {
        pagerAdapter?.scrollToTop()
    }

    fun searchByDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {

    }
}