package com.carbondev.tallynote.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.view.adapter.SellItemAdapter
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.datamodel.Library
import com.carbondev.tallynote.datamodel.Product
import com.carbondev.tallynote.datamodel.Sell
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.carbondev.tallynote.utils.DateTimeString
import com.carbondev.tallynote.utils.SCal

class SellCartViewModel: ViewModel() {

    private var remoteDataRepo = FirebaseDataRepository
    private var sellItemAdapter : SellItemAdapter = SellItemAdapter()

    val openCustomer : LiveData<Customer> = remoteDataRepo.singleCustomer
    val openSell : LiveData<Sell> = remoteDataRepo.singleSell
    val queryLibraryList : LiveData<ArrayList<Library>> = FirebaseDataRepository.queryLibraryList

    private var previousPaid = "0"

    var inputProduct = MutableLiveData<Product>().apply { value = Product() }

    val cursorGoUp = MutableLiveData<Boolean>()
    val showPaymentForm = MutableLiveData<Boolean>()
    val payButtonClick = MutableLiveData<Boolean>()
    val equalPressOnCalculator = MutableLiveData<Boolean>()

    val librarySearchText = MutableLiveData<String>()
    val libraryAddItem = MutableLiveData<String>()
    val libraryItemClick = MutableLiveData<Library>()
    var isName = true


    fun init(key : String){
        remoteDataRepo.fetchSingleCustomer(key)
        remoteDataRepo.fetchEmptySell()
    }

    // called from sell cart activity layout recycler view
    fun getSellItemAdapter(): SellItemAdapter {
        return sellItemAdapter
    }

    // add single product item to sell cart list
    fun onProductAddClick(){
        if (inputProduct.value!!.productName.isNotEmpty() && inputProduct.value!!.price.isNotEmpty() ){
            equalPressOnCalculator.value = true

            if (openSell.value!!.key.isEmpty()){
                createSellAddProduct() // add sell and products to database
            } else {
                addProductToCurrentSell() // add product to current sell cart list
            }
        }
    }

    // add total sell cart list to database
    // update customer
    // for first product of sell curt list
    private fun createSellAddProduct(){
        println("createSellAddProduct")
        val sell = Sell()
        val product = inputProduct.value!!
        val customer = openCustomer.value!!

        sell.customerKey = openCustomer.value!!.key
        sell.customerName = openCustomer.value!!.name
        sell.date = DateTimeString().now()
        sell.products = arrayListOf(product)
        sell.totalPrice = product.price
        sell.due = product.price

        val sc = SCal(customer.totalTransaction, product.price)
        customer.totalTransaction = sc.add()

        val sc2 = SCal(customer.totalDue, product.price)
        val td = sc2.add()

        customer.totalDue = td  // total due after sell in customer
        sell.beforeDue = td // total due after sell in sell history
        sell.afterDue = td

        remoteDataRepo.saveSell(sell)
        remoteDataRepo.updateCustomer(customer)

        inputProduct.value = Product()
        cursorGoUp.value = true
    }

    // add product to current sell cart list
    // update sell and customer to database
    // for non-first product of sell curt list
    private fun addProductToCurrentSell(){
        println("addProductToCurrentSell")
        val product = inputProduct.value!!
        val sell = openSell.value!!
        val customer = openCustomer.value!!

        sell.products.add(product)

        val sc1 = SCal(sell.totalPrice, product.price)
        sell.totalPrice = sc1.add()

        val sc2 = SCal(sell.due, product.price)
        sell.due = sc2.add()

        val sc3 = SCal(customer.totalTransaction, product.price)
        customer.totalTransaction = sc3.add()

        val sc4 = SCal(customer.totalDue, product.price)
        val td = sc4.add()

        customer.totalDue = td
        sell.beforeDue = td
        sell.afterDue = td

        remoteDataRepo.updateSell(sell)
        remoteDataRepo.updateCustomer(customer)
        this.inputProduct.value = Product()
        cursorGoUp.value = true
    }

    // add paid amount for current sell curt list
    fun addPaymentToSell(payAmount: String = "0", payAll : Boolean = false) {

        val s = openSell.value!!
        val c = openCustomer.value!!

        if (payAll || s.totalPrice.toDouble() <= payAmount.toDouble()) {
            s.paid = s.totalPrice
            s.due = "0"

            if (previousPaid != "0"){
                val sc4 = SCal(c.totalDue, previousPaid)
                val td = sc4.add()
                c.totalDue = td
                s.afterDue = td
            }

            val sc3 = SCal(c.totalDue, s.totalPrice)
            val td2 = sc3.sub()
            c.totalDue = td2
            s.afterDue = td2

            previousPaid = s.totalPrice

        } else {
            s.paid = payAmount


            // minus paid from sell due
            val sc1 = SCal(s.totalPrice, payAmount)
            s.due = sc1.sub()

            // if previously paid for this sell then remove previous paid for pay again
            if (previousPaid != "0"){
                val sc3 = SCal(c.totalDue, previousPaid)
                val td = sc3.add()
                c.totalDue = td
                s.afterDue = td
            }

            // minus paid amount from customer total due
            val sc2 = SCal(c.totalDue, payAmount)
            val td2 = sc2.sub()
            c.totalDue = td2
            s.afterDue = td2

            previousPaid = payAmount

        }

        remoteDataRepo.updateSell(s)
        remoteDataRepo.updateCustomer(c)
    }


    fun refreshProductList(){
        sellItemAdapter.setData(openSell.value!!.products)
    }

    fun onShowPaymentForm() {
        showPaymentForm.value = !(showPaymentForm.value ?: false)
    }

    fun onPayButtonClick(){
        payButtonClick.value = true
    }

    fun getLibraryFilteredList(queryText : String){
        remoteDataRepo.getLibraryFilteredList(queryText, isName)
    }

    fun onLibraryItemClick(library : Library) {
        libraryItemClick.value = library
    }

    fun autoFillUpProduct(library: Library){
        val p = inputProduct.value
        if (isName)
            p?.productName = library.detail
        else
            p?.detail = library.detail

        inputProduct.value = p
    }

    fun addLibraryItem(text: String?) {
        text?.let { remoteDataRepo.addLibraryItem(it, isName) }
    }
}

