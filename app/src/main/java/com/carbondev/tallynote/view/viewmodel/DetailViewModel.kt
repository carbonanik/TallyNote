package com.carbondev.tallynote.view.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.R
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.datamodel.Sell
import com.carbondev.tallynote.datamodel.typePayment
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.carbondev.tallynote.utils.DateTimeString
import com.carbondev.tallynote.utils.MyDateFormat
import com.carbondev.tallynote.utils.StringNumberCalculator
import com.carbondev.tallynote.view.adapter.CustomerDetailPagerAdapter
import com.carbondev.tallynote.view.adapter.HistorySellAdapter
import java.util.*
import kotlin.math.abs

class DetailViewModel() : ViewModel() {

    var context: Context? = null
    private val remoteDataRepo = FirebaseDataRepository

    private var historySellAdapter : HistorySellAdapter? = null
    private var detailPagerAdapter : CustomerDetailPagerAdapter? = null

    val currentCustomer : LiveData<Customer>
        get() = remoteDataRepo.singleCustomer

    val sellList : LiveData<ArrayList<Sell>>
        get() = remoteDataRepo.allSell

    private val allDateMillis : MutableList<Long> = arrayListOf()

    val dueOrAdv = MutableLiveData<String>()
    val dueOrAdvAmount = MutableLiveData<String>()
//    private val pickedDate = MutableLiveData<String>()

    val onEditCustomerClick = MutableLiveData<Boolean>()
    val onCartClick = MutableLiveData<Boolean>()
    val payDueButtonClick = MutableLiveData<Boolean>()
    val onDatePickerClick = MutableLiveData<Boolean>()
    val onDeleteCustomerClick = MutableLiveData<Boolean>()


    fun init(customerKey: String, context: Context){
        this.context = context
        declareAdapters()

        emptyCustomer()
        emptySellList()

        getSingleCustomer(customerKey)
        getAllSellForThisCustomer(customerKey)
    }

    private fun getSingleCustomer(key: String){
        remoteDataRepo.fetchSingleCustomer(key)
    }

    private fun getAllSellForThisCustomer(customerKey: String){
        remoteDataRepo.fetchAllSellForThisCustomer(customerKey)
    }

    fun notifySellAdapter(){
        historySellAdapter!!.notifyDataSetChanged()
    }


    fun getHistorySellAdapter(): HistorySellAdapter? {
        return historySellAdapter
    }

    fun getDetailPagerAdapter(): CustomerDetailPagerAdapter? {
        return detailPagerAdapter
    }

    fun onEditCustomerClick() {
        onEditCustomerClick.value = true
    }

    fun onCartClick(){
        onCartClick.value = true
    }

    fun onPayDueButtonClick(){
        payDueButtonClick.value = true
    }

    fun onDatePickerClick(){
        onDatePickerClick.value = true
    }

    fun onDeleteCustomerClick() {
        onDeleteCustomerClick.value = true
    }

    fun payDue(payAmount: String, note: String) {
        val sell = Sell() // Empty Sell
        // due before this transaction
        sell.beforeDue = currentCustomer.value!!.totalDue
        //update openCustomer total due
        val sc = StringNumberCalculator(currentCustomer.value!!.totalDue, payAmount)
        currentCustomer.value!!.totalDue = sc.sub()

        //add value to sell
        sell.customerKey = currentCustomer.value!!.key
        sell.customerName = currentCustomer.value!!.name
        sell.type = typePayment
        sell.payment = payAmount
        sell.afterDue = currentCustomer.value!!.totalDue
        sell.date = DateTimeString().now()
        sell.note = note

        remoteDataRepo.saveSell(sell)
        remoteDataRepo.updateCustomer(currentCustomer.value!!)
    }

    fun submitUpdatedCustomer(customer: Customer) {
        remoteDataRepo.updateCustomer(customer)
    }


    private fun declareAdapters(){
        detailPagerAdapter      = CustomerDetailPagerAdapter(this)
        historySellAdapter      = HistorySellAdapter(this)
    }

    fun calculateDueOrAdv(){
        val d = currentCustomer.value!!.totalDue
        if (d[0] == '-'){
            dueOrAdv.value = context?.getString(R.string.adv)
            dueOrAdvAmount.value = d.substring(1)
        } else{
            dueOrAdv.value = context?.getString(R.string.due)
            dueOrAdvAmount.value = d
        }
    }

    fun searchByDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        getAllDate()

        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth)
//        val m = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

//        pickedDate.value = "Select The Date You Want To Go"//"$dayOfMonth/$m/$year"
        val position = findClosest(calendar.timeInMillis)
        detailPagerAdapter?.goSearchPosition(position)
    }

    private fun getAllDate(){
        allDateMillis.clear()
        sellList.value?.forEachIndexed { _, sell ->
            val date = sell.date
            val myDateFormat = MyDateFormat(date)
            val millis = myDateFormat.getMillis()
            allDateMillis.add(millis)


        }
    }

    private fun findClosest(myMilli : Long): Int {
        var distance : Long = abs(allDateMillis[0] - myMilli)
        var index = 0
        for (count in 1 until allDateMillis.size) {
            val closestDistance = abs(allDateMillis[count] - myMilli)
            if (closestDistance < distance) {
                index = count
                distance = closestDistance
            }
        }
        return index
    }

    fun scrollToTop(){
        detailPagerAdapter!!.scrollToTop()
    }

    private fun emptyCustomer(){
        remoteDataRepo.fetchEmptySingleCustomer()
    }

    private fun emptySellList(){
        remoteDataRepo.fetchEmptySellList()
    }

    fun deleteCustomer() {
        if (!currentCustomer.value?.key.isNullOrEmpty()){
            remoteDataRepo.deleteCustomer(currentCustomer.value!!.key)
        }
    }
}
















