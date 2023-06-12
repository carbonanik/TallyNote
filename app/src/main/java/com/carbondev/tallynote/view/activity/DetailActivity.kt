package com.carbondev.tallynote.view.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.ActivityDetailBinding
import com.carbondev.tallynote.databinding.AddCustomerBinding
import com.carbondev.tallynote.databinding.PayCustomerDialogBinding
import com.carbondev.tallynote.datamodel.*
import com.carbondev.tallynote.view.viewmodel.DetailViewModel
import java.util.*


class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel : DetailViewModel
    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setupBinding(savedInstanceState)

        tabButtonListener()
        setObserver()
    }

    override fun onRestart() {
        viewModel.notifySellAdapter()
        super.onRestart()
    }

    private fun setupBinding(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        if (savedInstanceState == null) {
            viewModel.init(getCustomerKeyIntent(), this)
        }

        binding = DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)
            .apply {
                this.lifecycleOwner = this@DetailActivity
                this.detailViewModel = viewModel
//                this.customerDetailPager.adapter = CustomerDetailPagerAdapter(viewModel, this@DetailActivity)
            }
    }

    private fun getCustomerKeyIntent(): String {
        return intent.getStringExtra(INTENT_CUSTOMER_DETAIL) ?: ""

    }


    private fun tabButtonListener() {

        if (binding.customerDetailPager.currentItem == HISTORY_TAB) {
            binding.tabHistory.animate().scaleX(1.5F).scaleY(1.5F)
            binding.tabAbout.bringToFront()
        }

        binding.tabHistory.setOnClickListener {
            binding.customerDetailPager.currentItem = HISTORY_TAB
        }

        binding.tabAbout.setOnClickListener {
            binding.customerDetailPager.currentItem = ABOUT_TAB
        }

        binding.customerDetailPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(position: Int) {}
            override fun onPageScrolled(position: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(position: Int) {

                if (position == HISTORY_TAB) {
                    binding.tabHistory.animate().scaleX(1.5F).scaleY(1.5F)
                    binding.tabAbout.animate().scaleX(1F).scaleY(1F)
                    binding.tabAbout.bringToFront()
                    binding.calenderButton.visibility = View.VISIBLE
                }

                if ( position == ABOUT_TAB) {
                    binding.tabHistory.animate().scaleX(1F).scaleY(1F)
                    binding.tabAbout.animate().scaleX(1.5F).scaleY(1.5F)
                    binding.tabHistory.bringToFront()
                    binding.calenderButton.visibility = View.INVISIBLE
                }
            }
        })

        binding.backButton.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun setObserver() {
        viewModel.onEditCustomerClick.observe(this) {

            val editCustomerDialog = AlertDialog.Builder(this).create()
            val view = layoutInflater.inflate(R.layout.add_customer, null)
            val binding = AddCustomerBinding.bind(view)

            binding.customer = viewModel.currentCustomer.value
            binding.typeNote.text = getString(R.string.edit_customer)
            if (viewModel.currentCustomer.value?.gender == Gender().male) {
                binding.radioBMale.isChecked = true
            } else if (viewModel.currentCustomer.value?.gender == Gender().female) {
                binding.radioBFemale.isChecked = true
            }

            editCustomerDialog.setView(view)
            binding.addButton.setOnClickListener {
                val customer = binding.customer
                if (customer!!.name.isNotEmpty()) {
                    viewModel.submitUpdatedCustomer(customer)
                }
                editCustomerDialog.dismiss()
            }

            binding.cancelButton.setOnClickListener {
                editCustomerDialog.dismiss()
            }
            editCustomerDialog.show()
        }

        viewModel.onCartClick.observe( this) {
            val intent = Intent(this, SellCartActivity::class.java)
            intent.putExtra(INTENT_SELL_CURT, viewModel.currentCustomer.value?.key)
            startActivity(intent)
        }

        viewModel.payDueButtonClick.observe( this) {

            val payDialog = AlertDialog.Builder(this).create()
            val view = layoutInflater.inflate(R.layout.pay_customer_dialog, null)
            val payCustomerDialogBinding = PayCustomerDialogBinding.bind(view)

            payDialog.setView(view)
            payCustomerDialogBinding.addButton.setOnClickListener {

                val payAmount = payCustomerDialogBinding.inputPayNumber

                val note = payCustomerDialogBinding.note

                if (!payAmount.isNullOrEmpty()) {
                    if (note.isNullOrEmpty()) {
                        viewModel.payDue(payAmount, "")
                    } else {
                        viewModel.payDue(payAmount, note)
                    }
                }
                payDialog.dismiss()
            }
            payCustomerDialogBinding.cancelButton.setOnClickListener {
                payDialog.dismiss()
            }
            payDialog.show()
        }

        viewModel.currentCustomer.observe(this) {
            viewModel.calculateDueOrAdv()
        }

        viewModel.sellList.observe(this) {
            viewModel.notifySellAdapter()
            viewModel.scrollToTop()
        }

        viewModel.onDatePickerClick.observe(this) {
            handleDateButtonClick()
        }

        viewModel.onDeleteCustomerClick.observe(this) {
            MaterialDialog(this).show {
                title(text = "Delete Customer?")
                message(text = "All Data Of this Customer will be deleted")
                positiveButton(text = "Confirm!") {
                    viewModel.deleteCustomer()
                }
                negativeButton(text = "Cancel")

                lifecycleOwner(this@DetailActivity)

            }
        }
    }

    private fun handleDateButtonClick(){

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog( this, { _, pickedYear, monthOfYear, dayOfMonth ->

            viewModel.searchByDate(pickedYear, monthOfYear, dayOfMonth)

        }, year, month, day )

        datePickerDialog.show()
    }
}