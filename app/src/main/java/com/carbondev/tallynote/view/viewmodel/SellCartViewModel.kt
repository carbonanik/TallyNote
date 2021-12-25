package com.carbondev.tallynote.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.datamodel.Library
import com.carbondev.tallynote.datamodel.Product
import com.carbondev.tallynote.datamodel.Sell
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.carbondev.tallynote.utils.DateTimeString
import com.carbondev.tallynote.view.adapter.SellItemAdapter

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

    // Creating New Sell Adding First Product
    private fun createSellAddProduct(){
        val sell = Sell()
        val product = inputProduct.value!! // user input for product
        val customer = openCustomer.value!! //

        sell.customerKey = openCustomer.value!!.key // reference of customer key
        sell.customerName = openCustomer.value!!.name
        sell.date = DateTimeString().now()
        sell.products = arrayListOf(product)
        sell.totalPrice = product.price
        sell.due = product.price
        sell.beforeDue = customer.totalDue // total due before this transaction

        customer.totalTransaction = add(customer.totalTransaction, product.price)

        val afterTotalDue = add(customer.totalDue, product.price)
        customer.totalDue = afterTotalDue  // total due after sell in customer
        sell.afterDue = afterTotalDue

        remoteDataRepo.saveSell(sell)
        remoteDataRepo.updateCustomer(customer)

        inputProduct.value = Product()
        cursorGoUp.value = true
    }

    // add product to current sell cart list
    // update sell and customer to database
    // for non-first product of sell curt list
    private fun addProductToCurrentSell(){
        val product = inputProduct.value!!
        val sell = openSell.value!!
        val customer = openCustomer.value!!

        sell.products.add(product)
        sell.totalPrice = add(sell.totalPrice, product.price) // add product price to sell total price
        sell.due = add(sell.due, product.price) // add product price to due amount
        customer.totalTransaction = add(customer.totalTransaction, product.price) //add product price to customers total transaction

        val totalDue = add(customer.totalDue, product.price) // total due after adding this product to sell
        customer.totalDue = totalDue
        sell.afterDue = totalDue

        remoteDataRepo.updateSell(sell)
        remoteDataRepo.updateCustomer(customer)
        this.inputProduct.value = Product() // clear product input field
        cursorGoUp.value = true
    }

    // add paid amount for current sell curt list
    fun addPaymentToSell(payAmount: String = "0", payAll : Boolean = false) {

        val sell = openSell.value!!
        val customer = openCustomer.value!!

        if (payAll || sell.totalPrice.toDouble() <= payAmount.toDouble()) {
            sell.paid = sell.totalPrice
            sell.due = "0"

            if (previousPaid != "0"){
                val td = add(customer.totalDue, previousPaid)
                customer.totalDue = td
                sell.afterDue = td
            }

            val td2 = sub(customer.totalDue, sell.totalPrice)
            customer.totalDue = td2
            sell.afterDue = td2

            previousPaid = sell.totalPrice

        } else {
            sell.paid = payAmount


            // minus paid from sell due
            sell.due = sub(sell.totalPrice, payAmount)

            // if previously paid for this sell then remove previous paid for pay again
            if (previousPaid != "0"){
                val td = add(customer.totalDue, previousPaid)
                customer.totalDue = td
                sell.afterDue = td
            }

            // minus paid amount from customer total due
            val td2 = sub(customer.totalDue, payAmount)
            customer.totalDue = td2
            sell.afterDue = td2

            previousPaid = payAmount

        }

        remoteDataRepo.updateSell(sell)
        remoteDataRepo.updateCustomer(customer)
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

    private fun add(firstNumber: String, secondNumber: String) = String.format("%.0f", (firstNumber.toDouble() + secondNumber.toDouble()))
    private fun sub(firstNumber: String, secondNumber: String) = String.format("%.0f",(firstNumber.toDouble() - secondNumber.toDouble()))

}

