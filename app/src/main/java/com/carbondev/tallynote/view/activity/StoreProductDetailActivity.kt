package com.carbondev.tallynote.view.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.*
import com.carbondev.tallynote.datamodel.ABOUT_TAB
import com.carbondev.tallynote.datamodel.HISTORY_TAB
import com.carbondev.tallynote.datamodel.INTENT_STORE_PRODUCT_DETAIL
import com.carbondev.tallynote.datamodel.StoreProductModified
import com.carbondev.tallynote.utils.DateTimeString
import com.carbondev.tallynote.view.viewmodel.StoreProductDetailViewModel
import java.util.*

class StoreProductDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: StoreProductDetailViewModel
    private lateinit var binding: ActivityStoreProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_product_detail)
        setupBinding(savedInstanceState)

        tabButtonListener()
        setObserver()
    }

    override fun onRestart() {
        viewModel.notifyHistoryAdapter()
        super.onRestart()
    }

    private fun setupBinding(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(StoreProductDetailViewModel::class.java)

        if (savedInstanceState == null) {
            viewModel.init(getStoreProductKeyIntent())
        }

        binding = DataBindingUtil.setContentView<ActivityStoreProductDetailBinding>(
            this,
            R.layout.activity_store_product_detail
        )
            .apply {
                this.lifecycleOwner = this@StoreProductDetailActivity
                this.viewModel = this@StoreProductDetailActivity.viewModel
//                this.customerDetailPager.adapter = CustomerDetailPagerAdapter(viewModel, this@DetailActivity)
            }
    }

    private fun getStoreProductKeyIntent(): String {
        return intent.getStringExtra(INTENT_STORE_PRODUCT_DETAIL) ?: ""
    }


    private fun tabButtonListener() {

//
        if (binding.storeProductDetailPager.currentItem == HISTORY_TAB) {
            binding.storeProductTabHistory.animate().scaleX(1.5F).scaleY(1.5F)
            binding.storeProductTabAbout.bringToFront()
        }

        binding.storeProductTabHistory.setOnClickListener {
            binding.storeProductDetailPager.currentItem = HISTORY_TAB
        }

        binding.storeProductTabAbout.setOnClickListener {
            binding.storeProductDetailPager.currentItem = ABOUT_TAB
        }

        binding.storeProductDetailPager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {}
            override fun onPageScrolled(position: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(position: Int) {

                if (position == HISTORY_TAB) {
                    binding.storeProductTabHistory.animate().scaleX(1.5F).scaleY(1.5F)
                    binding.storeProductTabAbout.animate().scaleX(1F).scaleY(1F)
                    binding.storeProductTabAbout.bringToFront()
                    binding.calenderButton.visibility = View.VISIBLE
                }

                if (position == ABOUT_TAB) {
                    binding.storeProductTabHistory.animate().scaleX(1F).scaleY(1F)
                    binding.storeProductTabAbout.animate().scaleX(1.5F).scaleY(1.5F)
                    binding.storeProductTabHistory.bringToFront()
                    binding.calenderButton.visibility = View.INVISIBLE
                }
            }
        })

        binding.backButton.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun setObserver() {
        viewModel.onEditStoreProductClick.observe(this) {

            val dialog = AlertDialog.Builder(this).create()
            val view = layoutInflater.inflate(R.layout.add_store_product, null)
            val binding = AddStoreProductBinding.bind(view)

            binding.storeProduct = viewModel.currentStoreProduct.value
            binding.typeNote.text = getString(R.string.edit_product)

            dialog.setView(view)
            binding.addButton.setOnClickListener {
                val storeProduct = binding.storeProduct
                if (storeProduct!!.name.isNotEmpty()) {
                    viewModel.submitUpdatedStoreProduct(storeProduct)
                }
                dialog.dismiss()
            }

            binding.cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        viewModel.onBuyClick.observe(this) {
            newStoreProductModifiedDialog(
                isAdded = true,
            )
        }

        viewModel.onSellClick.observe(this) {
            newStoreProductModifiedDialog(
                isAdded = false,
            )
        }

        viewModel.currentStoreProduct.observe(this) {
//            viewModel.calculateDueOrAdv()
        }

        viewModel.allStoreProductModified.observe(this) {
            viewModel.notifyHistoryAdapter()
            viewModel.scrollToTop()
        }

        viewModel.onDatePickerClick.observe(this) {
//            handleDateButtonClick()

        }

        viewModel.onDeleteStoreProductClick.observe(this) {
            MaterialDialog(this).show {
                title(text = "Delete Store Product?")
                message(text = "All Data Of this Customer will be deleted")
                positiveButton(text = "Confirm!") {
                    viewModel.deleteStoreProduct()
                }
                negativeButton(text = "Cancel")

                lifecycleOwner(this@StoreProductDetailActivity)

            }
        }
    }

    private fun newStoreProductModifiedDialog(isAdded: Boolean) {
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
                viewModel.storeProductModified(modified!!)
            }
            dialog.dismiss()
        }
        viewBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun handleDateButtonClick() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, pickedYear, monthOfYear, dayOfMonth ->

            viewModel.searchByDate(pickedYear, monthOfYear, dayOfMonth)

        }, year, month, day)

        datePickerDialog.show()
    }
}