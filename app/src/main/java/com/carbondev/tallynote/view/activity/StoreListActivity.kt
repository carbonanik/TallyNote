package com.carbondev.tallynote.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.ActivityStoreListBinding
import com.carbondev.tallynote.databinding.AddStoreProductBinding
import com.carbondev.tallynote.databinding.AddStoreProductModifiedBinding
import com.carbondev.tallynote.datamodel.INTENT_STORE_PRODUCT_DETAIL
import com.carbondev.tallynote.datamodel.StoreProduct
import com.carbondev.tallynote.datamodel.StoreProductModified
import com.carbondev.tallynote.utils.DateTimeString
import com.carbondev.tallynote.view.viewmodel.StoreListViewModel

class StoreListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreListBinding
    private lateinit var viewModel: StoreListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_list)

        setUpBinding(savedInstanceState)

        observers()
    }

    private fun setUpBinding(savedInstanceState: Bundle?) {
        val linearLayoutManager = LinearLayoutManager(this)

        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        viewModel = ViewModelProvider(this).get(StoreListViewModel::class.java)
        binding = DataBindingUtil.setContentView<ActivityStoreListBinding?>(
            this,
            R.layout.activity_store_list
        ).apply {
            lifecycleOwner = this@StoreListActivity
            storeListViewModel = viewModel
            storeRecyclerVew.layoutManager = linearLayoutManager
        }

        if (savedInstanceState == null) {
            viewModel.init()
        }
    }

    private fun observers() {
        viewModel.onAddStoreProductClick.observe(this) {

            val dialog = AlertDialog.Builder(this).create()
            val view = layoutInflater.inflate(R.layout.add_store_product, null)
            val addStoreProductBinding = AddStoreProductBinding.bind(view)

            addStoreProductBinding.storeProduct = StoreProduct()
            dialog.setView(view)
            addStoreProductBinding.addButton.setOnClickListener {

                val storeProduct = addStoreProductBinding.storeProduct

                if (storeProduct!!.name.isNotEmpty()) {
                    viewModel.addNewStoreProduct(storeProduct)

//                    newCustomerAdded = true
                }
                dialog.dismiss()
            }

            addStoreProductBinding.cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        viewModel.liveStoreProductList.observe(this) {
            viewModel.storeProducts.value = viewModel.liveStoreProductList.value
            viewModel.refreshList()
//            if (newCustomerAdded) {
//                scrollToTop()
//                newCustomerAdded = false
//            }
        }

        viewModel.storeProducts.observe(this) {

            viewModel.refreshList()
        }

        viewModel.onItemClick.observe(this) {
            val intent = Intent(this, StoreProductDetailActivity::class.java)
            intent.putExtra(INTENT_STORE_PRODUCT_DETAIL, it)
            startActivity(intent)
        }

        viewModel.onItemAddButtonClick.observe(this) { storeProductKey ->
            newStoreProductModifiedDialog(
                storeProductKey,
                isAdded = true,
            )
        }

        viewModel.onItemRemoveButtonClick.observe(this) { storeProductKey ->
            newStoreProductModifiedDialog(
                storeProductKey,
                isAdded = false,
            )
        }
    }

    private fun newStoreProductModifiedDialog(storeProductKey: String, isAdded: Boolean) {
        val dialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.add_store_product_modified, null)
        val viewBinding = AddStoreProductModifiedBinding.bind(view)

        if (isAdded) viewBinding.storeProductModifiedType.text = getString(R.string.buy)
        else viewBinding.storeProductModifiedType.text = getString(R.string.sell)

        viewBinding.storeProductModified = StoreProductModified()

        dialog.setView(view)
        viewBinding.addButton.setOnClickListener {

            val modified = viewBinding.storeProductModified
            modified?.isAdded = isAdded
            modified?.dateTime = DateTimeString().now()

            if (!modified?.count.isNullOrEmpty()) {
                viewModel.storeProductModified(modified!!, storeProductKey)
            }
            dialog.dismiss()
        }
        viewBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onRestart() {
        viewModel.refreshList()
        super.onRestart()
    }


}